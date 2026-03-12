# 운영(AWS) 배포 가이드 (Docker Hub 이미지 pull 방식)

## 구성 요약

| 구성요소 | 위치 | 비고 |
|----------|------|------|
| DB (MySQL) | AWS 관리형 DB | Compose 제외, 연결 정보만 env로 전달 |
| Elasticsearch | 개인 PC | Compose 제외, ELASTICSEARCH_HOST로 URL 전달 |
| Nginx | 같은 AWS 서버 | 80/443 리버스 프록시, 인증서는 `nginx/certs/` 에 둠 |
| geonpil + search-service | 같은 AWS 서버 | Docker Hub 이미지 pull 후 `docker-compose.prod.yml` 로 실행 |

---

## 진행 순서

### 1. 사전 준비

- [ ] **Docker Hub** 계정 생성, 로컬에서 `docker login` 완료
- [ ] **AWS 관리형 DB** 생성 후 접속 가능한 URL/계정/비밀번호 확보  
  - 예: `jdbc:mysql://your-rds-endpoint.region.rds.amazonaws.com:3306/dbGeonpil?serverTimezone=Asia/Seoul`
- [ ] **개인 PC에 ES** 설치·실행, AWS 서버에서 접근 가능하도록 설정  
  - 방화벽에서 9200(또는 사용 포트) 허용, 공인 IP 또는 DDNS 확인  
  - HTTPS 사용 시 인증서/truststore 필요 여부 결정

### 2. 로컬에서 이미지 빌드 & Docker Hub push

Docker Hub 사용자명을 `YOUR_DOCKERHUB_ID` 라고 할 때:

**geonpil (이 레포):**
```bash
cd C:\geonpil2\geonpil
docker build -t YOUR_DOCKERHUB_ID/geonpil:latest .
docker push YOUR_DOCKERHUB_ID/geonpil:latest
```

**search-service:**
```bash
cd C:\geonpil2\search-service
docker build -t YOUR_DOCKERHUB_ID/search-service:latest .
docker push YOUR_DOCKERHUB_ID/search-service:latest
```

배포할 때마다 위처럼 빌드 후 `push` 하면, 운영 서버에서 `pull` 로 최신 이미지를 받을 수 있음.

### 3. 운영 서버(AWS)에 올릴 것

서버 배포 디렉터리(예: `/opt/geonpil`)에 아래를 둠.

- `docker-compose.prod.yml` (이 레포에서 복사)
- `.env` (환경변수, 아래 예시 참고)
- `nginx/` 폴더
  - `nginx/conf.d/default.conf` (Nginx 설정)
  - `nginx/certs/` (HTTPS 쓸 때 인증서: `fullchain.pem`, `privkey.pem`)

### 4. 환경변수 파일 생성 (AWS 서버에서)

같은 디렉터리에 `.env` 생성 예시:

```bash
# Docker Hub 이미지 접두어 (필수)
DOCKERHUB_USERNAME=YOUR_DOCKERHUB_ID

# AWS 관리형 DB (필수)
SPRING_DATASOURCE_URL=jdbc:mysql://<RDS엔드포인트>:3306/dbGeonpil?serverTimezone=Asia/Seoul
SPRING_DATASOURCE_USERNAME=geonpil_user
SPRING_DATASOURCE_PASSWORD=<비밀번호>

# 개인 PC Elasticsearch (필수)
ELASTICSEARCH_HOST=http://<개인PC공인IP또는DDNS>:9200
ELASTICSEARCH_USERNAME=elastic
ELASTICSEARCH_PASSWORD=<비밀번호>

# 메일 / OAuth (필요 시)
# MAIL_HOST=...
# OAUTH_NAVER_CLIENT_ID=...
# OAUTH_KAKAO_CLIENT_ID=...
```

HTTPS ES 사용 시:

- `ELASTICSEARCH_HOST=https://...` 로 설정
- self-signed 인증서라면 (간단 적용):
  - `ELASTICSEARCH_SSL_INSECURE=true`
  - `ELASTICSEARCH_HOST=https://...` 로 설정 (ES의 HTTPS 포트로)
- 인증서 검증까지 정상 적용하려면:
  - geonpil/search-service 컨테이너에 truststore 마운트 및 `JAVA_TOOL_OPTIONS` 설정 (compose 주석 해제 후 적용)
  - 아래 절차로 **운영 CA(`ca.crt`)를 truststore로 변환** 후 배포 디렉터리에 둠

#### (선택) self-signed CA로 truststore.jks 만들기 (운영 권장)

전제:
- 개인 PC(ES가 도는 곳)에서 운영용 인증서를 만들었고, 그 중 **CA 인증서 파일 `ca.crt`** 를 확보했다.
- AWS 배포 디렉터리(예: `/opt/geonpil`)에 `certs/` 폴더를 만들고 `ca.crt` 를 넣는다.

AWS 서버에서(배포 디렉터리 기준):

```bash
cd /opt/geonpil
mkdir -p certs
# ca.crt 를 certs/ca.crt 로 복사해 둔 뒤 진행

# keytool이 없을 수 있으니 Docker로 JDK를 띄워 truststore 생성
docker run --rm -v "$(pwd)/certs:/certs" eclipse-temurin:17-jdk \
  keytool -importcert -noprompt \
  -alias es-prod-ca \
  -file /certs/ca.crt \
  -keystore /certs/truststore.jks \
  -storepass changeit
```

그 다음 `.env`에 추가(예시):

```bash
ELASTICSEARCH_SSL_INSECURE=false
ELASTICSEARCH_TRUSTSTORE_PASSWORD=changeit
```

### 5. 실행 (운영 서버에서)

```bash
# Docker Hub에서 이미지 pull (공개 이미지면 로그인 불필요, 비공개면 docker login 선행)
docker compose -f docker-compose.prod.yml pull

# 컨테이너 기동
docker compose -f docker-compose.prod.yml up -d
```

배포 업데이트 시: `pull` 다시 한 뒤 `up -d` 하면 새 이미지로 재기동됨.

### 6. 확인

- **웹 접속**: `http://<AWS서버>` (80) 또는 HTTPS 사용 시 `https://<AWS서버>` (443)
- geonpil은 Nginx를 통해만 노출됨 (호스트 8080 미노출)
- search-service: 같은 서버 8081 (내부 통신용)

### 7. CI/CD (GitHub Actions) — 자동 배포

`main` 브랜치에 push 하면 **geonpil** 이미지를 빌드 → Docker Hub 푸시 → AWS 서버에서 `pull` 및 `up -d` 까지 자동 실행.

**① GitHub Secrets 설정**

레포지토리 → **Settings** → **Secrets and variables** → **Actions** → **New repository secret** 에서 아래 추가:

| Secret 이름 | 설명 | 예시 |
|-------------|------|------|
| `DOCKERHUB_USERNAME` | Docker Hub 로그인 ID | `gyuwonpark` |
| `DOCKERHUB_TOKEN` | Docker Hub 액세스 토큰 | [Docker Hub](https://hub.docker.com/settings/security) → New Access Token |
| `SSH_PRIVATE_KEY` | AWS 서버 SSH 개인키 전체 내용 | `-----BEGIN OPENSSH PRIVATE KEY-----` ... |
| `SERVER_HOST` | AWS 서버 접속 주소 | `ec2-xx-xx-xx-xx.ap-northeast-2.compute.amazonaws.com` 또는 IP |
| `SERVER_USER` | SSH 로그인 사용자 | `ubuntu` |
| `SERVER_SSH_PORT` | (선택) SSH 포트. 없으면 22 사용 | `22` |

**② AWS 서버에 SSH 공개키 등록**

- GitHub Actions에서 쓸 개인키에 대응하는 **공개키**를 서버 `~/.ssh/authorized_keys` 에 추가.
- 또는 새 키 쌍 생성: `ssh-keygen -t ed25519 -C "github-actions"` → 개인키를 `SSH_PRIVATE_KEY` 에, 공개키를 서버에 등록.

**③ 워크플로우 동작**

- **트리거**: `main` 브랜치에 push 시 (또는 Actions 탭에서 `workflow_dispatch` 수동 실행).
- **빌드**: 이 레포 기준으로 `docker build` → `gyuwonpark/geonpil:latest` (및 `@sha`) 푸시.
- **배포**: 서버 `/opt/geonpil` 에서 `docker compose -f docker-compose.prod.yml pull` 후 `up -d` 실행.

**④ search-service**

- search-service는 별도 레포이므로, 그 레포에도 비슷한 workflow를 두거나 수동으로 이미지 빌드·푸시 후, 서버에서 `compose pull` 시 함께 갱신되도록 하면 됨.

---

### 8. HTTPS 적용 (선택)

1. **인증서 발급** (예: Let's Encrypt)
   - 서버에서 certbot 사용: `sudo certbot certonly --standalone -d 도메인`  
     (80 포트 사용하므로 Nginx 중지 후 발급하거나, webroot 방식 사용)
   - 발급된 파일: `fullchain.pem`, `privkey.pem` → `nginx/certs/` 에 복사
2. **Nginx 설정 수정**
   - `nginx/conf.d/default.conf` 에서 HTTPS용 `server { listen 443 ssl; ... }` 블록 주석 해제
   - HTTP(80) 블록에서 `return 301 https://$host$request_uri;` 주석 해제
3. Nginx 재시작: `docker compose -f docker-compose.prod.yml restart nginx`
4. AWS 보안 그룹에서 **443** 인바운드 허용

---

## 배포 시 SSH 보안 (0.0.0.0/0 사용 시)

SSH(22)를 **소스 0.0.0.0/0**으로 열면 전 세계 어디서나 접속 시도가 가능해 **보안상 위험**합니다. 봇 스캔·무차별 대입 공격에 노출될 수 있으므로, 가능하면 아래처럼 제한하는 편이 좋습니다.

| 방식 | 보안 | 난이도 | 비고 |
|------|------|--------|------|
| **GitHub Actions IP만 허용** | 좋음 | 중 | [api.github.com/meta](https://api.github.com/meta) 의 `actions` IP 범위를 보안 그룹에 반영. IP가 가끔 바뀌면 규칙 수동/자동 갱신 필요. |
| **Self-hosted runner** | 매우 좋음 | 중상 | EC2 또는 VPC 내부에 GitHub Actions runner 설치 후, 배포 job만 그 runner에서 실행. SSH는 같은 VPC/사설망에서만 허용하면 됨. |
| **AWS Systems Manager (SSM)** | 매우 좋음 | 상 | EC2에 SSM Agent만 쓰고, 포트 22는 아예 안 열고 SSM으로 명령 실행. GitHub에서 [aws-actions/aws-ssm-run-command](https://github.com/aws-actions/aws-ssm-run-command) 등 활용. |
| **0.0.0.0/0 + 완화 조치** | 보통 | 하 | 비밀번호 로그인 비활성화, 키 인증만 사용, `fail2ban` 등으로 시도 제한, 필요 시 기본 포트 변경. |

**권장**: 여유 있으면 **GitHub Actions IP만 허용**하거나 **Self-hosted runner**를 두고, SSH는 그에 맞게만 열어두는 것을 추천합니다.

### 2번(Self-hosted runner) vs 3번(SSM) — 어떤 걸 쓸까?

| 구분 | 2번 Self-hosted runner | 3번 AWS SSM |
|------|------------------------|-------------|
| **워크플로 변경** | 거의 없음. 배포 job만 `runs-on: self-hosted` 등으로 바꾸면 됨. 기존 `appleboy/ssh-action` 그대로 사용 가능. | 배포 단계를 SSH 대신 SSM Run Command 등으로 바꿔야 함. (예: `aws-actions` 또는 AWS CLI로 `ssm send-command` 호출) |
| **추가 구성** | Runner를 설치할 서버 한 대 (기존 앱 서버에 같이 둘 수도 있음). | EC2 IAM 역할(SSM 권한), GitHub↔AWS 연동(OIDC 또는 액세스 키), SSM 문서/명령 형식 설계. |
| **SSH 포트** | Runner와 배포 대상이 같은 서버면 SSH 불필요(로컬에서 `docker compose`만 실행). 다른 서버면 SSH는 VPC 내부만 허용하면 됨. | 포트 22를 아예 안 열어도 됨. |
| **난이도** | 상대적으로 낮음. Runner 설치 → 레포에 등록 → job에 라벨 지정. | IAM·OIDC 이해 필요, 단계가 더 많음. |

**추천: 2번(Self-hosted runner)**  
- 지금 쓰는 워크플로(빌드→Docker Hub 푸시→SSH로 배포)를 거의 그대로 두고, **배포 job만** self-hosted runner에서 돌리면 됨.  
- 가장 간단한 형태는 **배포 대상 EC2와 같은 서버에 runner를 설치**하는 것. 그러면 배포 시 SSH로 다른 서버에 접속할 필요 없이, 그 서버에서 `docker compose pull && up -d`만 실행하면 됨.  
- 3번(SSM)은 SSH를 완전히 없애고 싶을 때 좋지만, 설정 분량과 난이도가 더 크므로, 먼저 2번으로 배포를 안정화한 뒤 필요하면 SSM으로 전환하는 편을 권장합니다.

### Self-hosted runner 설정 요약 (같은 서버에 둘 때)

1. **배포 대상 EC2**(예: `/opt/geonpil`이 있는 서버)에 접속(기존 SSH 등).
2. [GitHub 레포 → Settings → Actions → Runners](https://github.com/geonpil/geonpil/settings/actions/runners) 에서 **New self-hosted runner** 선택 후 나오는 안내대로 설치·실행 (Linux 예: `./run.sh` 또는 서비스 등록).
3. Runner에 라벨 부여 (예: `self-hosted`, `linux`, `deploy`).
4. 워크플로에서 **deploy** job만 `runs-on: [self-hosted]` 또는 해당 라벨로 지정.  
   - 같은 서버에서 돌리므로, 배포 단계는 **SSH 대신** `run: cd /opt/geonpil && sudo docker compose -f docker-compose.prod.yml pull && sudo docker compose -f docker-compose.prod.yml up -d` 처럼 로컬 명령만 실행하도록 바꿀 수 있음.
5. 이 서버의 보안 그룹에서는 **SSH(22) 인바운드를 제거**하거나, 필요한 관리용 IP로만 제한해도 됨 (GitHub 호스트 IP는 더 이상 필요 없음).

### Self-hosted runner 설치 절차 (Lightsail / Linux)

배포 대상 서버(Lightsail 인스턴스)에 한 번만 따라 하면 됩니다.

**1. 서버에 SSH 접속**

- Lightsail 콘솔에서 SSH 키 다운로드 또는 브라우저 터미널로 접속.

**2. GitHub에서 runner 설치 명령 확인**

- GitHub 레포 → **Settings** → **Actions** → **Runners** → **New self-hosted runner**
- OS를 **Linux**로 선택 후 나오는 명령어들을 복사 (아래는 예시).

**3. 서버에서 실행 (예: Ubuntu 계열)**

```bash
# runner 디렉터리 생성 (위치 자유, 예: 홈 아래)
mkdir -p ~/actions-runner && cd ~/actions-runner

# GitHub에서 안내하는 다운로드 및 설정 (예시 — 실제 URL/토큰은 GitHub 화면에서 복사)
# curl -o actions-runner-linux-x64-2.311.0.tar.gz -L https://github.com/actions/runner/releases/download/v2.311.0/actions-runner-linux-x64-2.311.0.tar.gz
# tar xzf ./actions-runner-linux-x64-2.311.0.tar.gz
# ./config.sh --url https://github.com/OWNER/REPO --token TOKEN

# 설정이 끝나면 실행 (포그라운드)
./run.sh
```

- `config.sh` 실행 시 레포 URL과 토큰을 GitHub에서 표시한 값으로 넣습니다.
- 라벨은 기본값(`self-hosted`, `Linux`, `X64`)만 써도 되고, 워크플로의 `runs-on: [self-hosted]`와 맞습니다.

**4. systemd 서비스로 등록 (재부팅 후에도 자동 실행)**

Runner가 이미 한 번 설정되어 있고 `./run.sh`로 동작 확인했다면, 같은 디렉터리에 있는 **`svc.sh`** 로 서비스를 설치합니다.

1. **현재 실행 중인 runner 중지**  
   터미널에서 `./run.sh`를 돌리고 있다면 **Ctrl+C**로 종료합니다.

2. **서비스 설치 및 시작** (runner 디렉터리에서 실행)

```bash
cd /opt/geonpil/actions-runner

# systemd 서비스로 등록 (부팅 시 자동 시작 포함)
sudo ./svc.sh install

# 서비스 시작
sudo ./svc.sh start
```

3. **상태 확인**

```bash
sudo ./svc.sh status
```

- `active (running)` 이면 정상입니다.  
- GitHub **Settings → Actions → Runners**에서도 runner가 **Idle**로 보이면 됩니다.

4. **자주 쓰는 명령**

| 명령 | 설명 |
|------|------|
| `sudo ./svc.sh status` | 서비스 상태 확인 |
| `sudo ./svc.sh stop` | 서비스 중지 |
| `sudo ./svc.sh start` | 서비스 시작 |
| `sudo ./svc.sh uninstall` | 서비스 제거 (runner는 그대로 남음) |

5. **(Ubuntu/Debian) needrestart 사용 시 (선택)**  
   job 실행 중에 needrestart가 서비스를 재시작하지 않도록 하려면:

```bash
echo '$nrconf{override_rc}{qr(^actions\.runner\..+\.service$)} = 0;' | sudo tee /etc/needrestart/conf.d/actions_runner_services.conf
```

**5. 확인**

- GitHub **Settings → Actions → Runners**에 녹색으로 "Idle" 상태의 runner가 보이면 성공.
- 이제 `master`에 push하거나 **Actions** 탭에서 워크플로를 수동 실행하면, **deploy** job은 이 서버에서 자동으로 실행됩니다.

**6. (선택) 보안**

- 배포만 이 runner로 한다면, Lightsail 방화벽에서 **SSH(22) 인바운드**를 꺼두거나, 관리용 IP로만 제한해도 됩니다. Runner는 서버 → GitHub 방향으로만 통신하므로 인바운드 SSH가 필요 없습니다.

### GitHub Actions IP 확인 방법

- **URL**: **https://api.github.com/meta**
- 브라우저나 `curl https://api.github.com/meta` 로 접속하면 JSON이 내려옵니다.
- JSON 안의 **`actions`** 키가 GitHub Actions 호스트(러너)가 쓰는 **IPv4 CIDR 목록**입니다.  
  예: `"actions": [ "4.148.0.0/16", "4.149.0.0/18", ... ]`
- AWS 보안 그룹에는 **인바운드 규칙**으로 다음을 추가하면 됩니다.  
  - 유형: **SSH**, 포트: **22**  
  - 소스: 위 `actions` 배열에 있는 **각 CIDR을 하나씩** 규칙으로 추가 (예: `4.148.0.0/16`, `4.149.0.0/18` …)
- **주의**: `actions` 목록이 수십~수백 개라, AWS 보안 그룹 **인바운드 규칙 상한(기본 60개)** 을 넘을 수 있습니다. 그럴 경우:
  - 규칙 수를 줄이기 위해 [GitHub 공식 문서](https://docs.github.com/en/actions/security-guides/about-github-hosted-runners#ip-addresses) 에서 요약된 범위가 있는지 확인하거나,
  - 규칙을 주기적으로 갱신하는 스크립트로 보안 그룹을 관리하거나,
  - **Self-hosted runner** 사용을 검토하는 것이 좋습니다.

---

## 트러블슈팅

### 컨테이너에 .env가 적용되지 않음 (MAIL, OAuth 등 빈 값)

**원인**  
배포 시 `sudo docker compose -f docker-compose.prod.yml up -d` 를 실행하면, **`sudo` 때문에 프로세스의 작업 디렉터리(cwd)가 `/opt/geonpil`이 아닌 다른 경로(예: `/root`)로 바뀔 수 있습니다.**  
Docker Compose는 **실행한 셸의 현재 디렉터리**를 프로젝트 디렉터리로 쓰고, 그 안의 `.env`만 자동으로 읽습니다. 그래서 cwd가 바뀌면 `/opt/geonpil/.env`를 못 찾고, `${MAIL_HOST}` 등이 모두 빈 값으로 치환되어 컨테이너에 전달됩니다.

**해결**  
워크플로에서 compose 실행 시 **프로젝트 디렉터리와 .env 파일을 명시**합니다.

- `--project-directory /opt/geonpil` → 상대 경로(./nginx 등)와 프로젝트 기준 경로 고정  
- `--env-file /opt/geonpil/.env` → 이 경로의 .env를 읽어서 compose 파일의 `${VAR}` 치환에 사용  

이렇게 하면 `sudo`를 써도 항상 `/opt/geonpil/.env`가 적용됩니다. (deploy.yml 에 이미 반영됨)

**서버에서 직접 확인하는 방법**  
env가 제대로 치환되는지 보려면 서버에서:

```bash
cd /opt/geonpil
sudo docker compose --project-directory /opt/geonpil --env-file /opt/geonpil/.env -f /opt/geonpil/docker-compose.prod.yml config
```

`config` 출력 결과의 `geonpil` 서비스 `environment` 아래에 `MAIL_HOST`, `OAUTH_NAVER_CLIENT_ID` 등이 **빈 문자열이 아닌 값**으로 나오면 정상입니다.

---

### `dial tcp xxx.xxx.xxx.xxx: I/O timeout` (배포 단계에서 SSH 실패)

GitHub Actions의 "Deploy to AWS" 단계에서 위 오류가 나면 **GitHub 러너 → AWS 서버(EC2) SSH(22) 연결이 막혀 있는 상태**입니다.

1. **AWS 보안 그룹 확인 (가장 흔한 원인)**  
   EC2 인스턴스에 붙은 보안 그룹 **인바운드**에 SSH(22)가 허용되어 있어야 합니다.
   - **소스**는 보안을 위해 다음 중 하나를 권장합니다.  
     - **[GitHub Actions IP 범위](https://api.github.com/meta)** (`actions` 항목의 CIDR 목록)만 허용 → IP 변경 시 보안 그룹 규칙 갱신 필요.  
     - **Self-hosted runner**를 쓸 경우: 해당 runner가 있는 서버/대역만 허용.  
   - 소스를 `0.0.0.0/0`으로 두면 배포는 쉽지만, 전 구간 SSH 노출이 되므로 위 "배포 시 SSH 보안" 절의 완화 조치(키만 사용, fail2ban 등)를 함께 적용하는 것이 좋습니다.

2. **SERVER_HOST 값 확인**  
   - `SERVER_HOST`에 넣은 주소가 현재 EC2의 **퍼블릭 IP** 또는 **퍼블릭 DNS**와 일치하는지 확인.  
   - 재부팅 시 퍼블릭 IP가 바뀌는 인스턴스면 **Elastic IP**를 붙이고, 그 주소를 `SERVER_HOST`에 사용하는 것이 좋습니다.

3. **EC2 상태**  
   - 인스턴스가 **실행 중(running)** 인지, 퍼블릭 IP가 할당되어 있는지 콘솔에서 확인.

4. **SSH 포트 변경한 경우**  
   - SSH를 22가 아닌 다른 포트로 쓰면, 워크플로에서 `appleboy/ssh-action`에 `port: <포트번호>` 를 지정해야 합니다.

- **DB 연결 실패**: 보안 그룹/ACL에서 AWS 서버 → RDS 3306 허용 여부 확인.
- **ES 연결 실패**: 개인 PC 방화벽·공유기 포트포워딩(9200), 공인 IP/DDNS 확인. AWS → 해당 IP 9200 허용 여부 확인.
- **search-service 연결 실패**: `docker compose -f docker-compose.prod.yml ps` 로 search-service 컨테이너 기동 여부 확인, geonpil과 같은 네트워크(geonpil-net) 사용 여부 확인.
- **80/443 연결 거부**: AWS 보안 그룹에서 80, 443 인바운드 허용. Nginx 로그: `docker compose -f docker-compose.prod.yml logs nginx`
