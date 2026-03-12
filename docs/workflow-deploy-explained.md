# deploy.yml 워크플로 구조 설명

GitHub Actions 워크플로 파일(`.github/workflows/deploy.yml`)의 문법과 구조를 단계별로 설명합니다.

---

## 1. 파일 형식: YAML

- **YAML**은 들여쓰기로 단계(계층)를 나타내는 설정 형식입니다.
- **공백(스페이스)** 으로 들여쓰기하며, 같은 단계는 같은 칸 수여야 합니다.
- `키: 값` 형태로 씁니다. `-` 는 리스트(배열) 한 항목입니다.

```yaml
key: value           # 단일 값
list:                # 리스트
  - item1
  - item2
nested:              # 중첩 객체
  inner_key: value
```

---

## 2. 전체 구조 요약

| 최상위 키 | 의미 |
|-----------|------|
| `name` | 워크플로 이름 (Actions 탭에 표시) |
| `on` | 언제 이 워크플로를 실행할지 (트리거) |
| `env` | 전체에서 쓸 환경 변수 (선택) |
| `jobs` | 실행할 작업들 (빌드, 배포 등) |

---

## 3. 항목별 설명

### `name: Build and Deploy`

- 이 워크플로의 표시 이름입니다.
- GitHub Actions 탭에서 보이는 제목입니다.

---

### `on:` — 트리거 (언제 실행할지)

```yaml
on:
  push:
    branches: [master]   # master 브랜치에 push될 때
  workflow_dispatch:     # Actions 탭에서 "Run workflow" 수동 실행 가능
```

- **`push: branches: [master]`**  
  `master` 브랜치에 push가 일어나면 이 워크플로가 자동으로 실행됩니다.
- **`workflow_dispatch`**  
  저장 후 GitHub 웹에서 Actions → 해당 워크플로 → "Run workflow"로 수동 실행할 수 있습니다.

---

### `env:` — 공통 환경 변수

```yaml
env:
  DOCKER_IMAGE_GEONPIL: geonpil
```

- 이 워크플로의 **모든 job**에서 `env.DOCKER_IMAGE_GEONPIL` 로 참조할 수 있습니다.
- 예: `${{ env.DOCKER_IMAGE_GEONPIL }}` → `geonpil`

---

### `jobs:` — 실행할 작업들

워크플로는 여러 **job**으로 나뉘고, 각 job은 여러 **step**으로 나뉩니다.

```
jobs
├── build-and-push   (1번 job)
│   └── steps: [ Checkout, Set up Buildx, Login, Build and push ]
└── deploy           (2번 job)
    └── steps: [ Deploy (same server) ]
```

---

### Job: `build-and-push`

```yaml
build-and-push:
  runs-on: ubuntu-latest    # GitHub이 제공하는 Ubuntu 머신에서 실행
  steps:                    # 이 job 안에서 순서대로 실행되는 단계들
    - name: Checkout
      uses: actions/checkout@v4
```

- **`runs-on: ubuntu-latest`**  
  이 job은 GitHub 호스트된 Ubuntu runner에서 실행됩니다.
- **`steps`**  
  한 job 안에서 위에서 아래로 순서대로 실행되는 단계 배열입니다.

#### Step의 두 가지 형태

| 방식 | 의미 | 예 |
|------|------|-----|
| **`uses:`** | 기존 액션(재사용 코드) 실행 | `uses: actions/checkout@v4` |
| **`run:`** | 셸에서 직접 명령 실행 | `run: npm install` |

- **`uses: actions/checkout@v4`**  
  저장소 코드를 runner 워크스페이스에 체크아웃합니다. 빌드할 소스가 필요할 때 씁니다.
- **`uses: docker/login-action@v3`** + **`with:`**  
  `with`는 그 액션에 넘기는 입력값입니다.  
  `username`, `password`에 Secret을 넣어 Docker Hub 로그인을 합니다.
- **`${{ ... }}`**  
  GitHub Actions **표현식**입니다.  
  - `secrets.DOCKERHUB_USERNAME` → 레포 Settings → Secrets에 저장한 값  
  - `env.DOCKER_IMAGE_GEONPIL` → 위에서 정의한 환경 변수  
  - `github.sha` → 이번 실행(커밋)의 SHA (고유 해시)

```yaml
tags: |
  ${{ secrets.DOCKERHUB_USERNAME }}/${{ env.DOCKER_IMAGE_GEONPIL }}:latest
  ${{ secrets.DOCKERHUB_USERNAME }}/${{ env.DOCKER_IMAGE_GEONPIL }}:${{ github.sha }}
```

- **`|`**  
  여러 줄 문자열을 그대로 쓸 때 사용합니다. 위는 두 줄의 태그를 정의합니다.

---

### Job: `deploy`

```yaml
deploy:
  runs-on: [self-hosted]   # 우리가 등록한 self-hosted runner에서 실행
  needs: build-and-push    # build-and-push가 성공한 뒤에만 실행
  steps:
    - name: Deploy (same server)
      run: |
        cd /opt/geonpil
        sudo docker compose -f docker-compose.prod.yml pull
        ...
```

- **`runs-on: [self-hosted]`**  
  이 job은 **레포에 등록한 self-hosted runner**에서만 실행됩니다.  
  runner가 배포 대상 서버에 있으면, 그 서버에서 아래 명령이 실행됩니다.
- **`needs: build-and-push`**  
  `build-and-push` job이 **성공으로 끝난 다음에만** `deploy`가 실행됩니다.  
  빌드/푸시가 실패하면 배포 단계는 건너뜁니다.
- **`run: |`**  
  셸에서 실행할 명령을 여러 줄로 쓴 것입니다.  
  runner가 설치된 서버(같은 서버라면 `/opt/geonpil`)에서 `docker compose pull` / `up -d` 등이 실행됩니다.

---

## 4. 실행 순서 요약

1. **트리거**: `master`에 push 또는 수동 "Run workflow"
2. **Job 1 — build-and-push** (GitHub 호스트 runner)
   - Checkout → Docker Buildx → Docker Hub 로그인 → 이미지 빌드 및 푸시
3. **Job 2 — deploy** (self-hosted runner, 1번 성공 시에만)
   - `/opt/geonpil`에서 `docker compose pull` → `up -d` → `ps`

---

## 5. 자주 쓰는 표현식

| 표현식 | 의미 |
|--------|------|
| `${{ secrets.이름 }}` | Settings → Secrets and variables → Actions 에 등록한 비밀값 |
| `${{ env.변수명 }}` | 이 워크플로의 `env`에 정의한 값 |
| `${{ github.sha }}` | 이번 실행의 커밋 SHA |
| `${{ github.ref }}` | 브랜치/태그 ref (예: refs/heads/master) |

이렇게 이해하면 `deploy.yml`의 문법과 구조를 따라가기 쉬울 것입니다.
