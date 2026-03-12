## geonpil

Spring Boot 기반 웹 애플리케이션 **`geonpil`** 백엔드 레포지토리입니다. 운영 환경에서는 `geonpil`과 별도 프로젝트인 `search-service`를 함께 구성해 **검색 기능을 서비스로 분리**하고, Docker Hub + GitHub Actions 기반으로 자동 배포합니다.

### 프로젝트 목표

- **기능 분리/확장성**: 검색 기능을 `search-service`로 분리해 독립 배포·스케일링 가능하도록 설계
- **운영 자동화**: push → 이미지 빌드/푸시 → 서버에서 compose 갱신까지 CI/CD 파이프라인 구성
- **운영 환경 최적화**: Nginx 리버스 프록시, 외부 DB(RDS), 외부 Elasticsearch 연동을 전제로 환경변수 중심으로 구성

### 기술 스택

- **Backend**: Java 17, Spring Boot 3.1.x
- **View**: Thymeleaf (+ layout dialect)
- **DB**: MySQL (운영은 AWS RDS 등 외부 DB 연결)
- **ORM/Mapper**: MyBatis
- **Search**: Spring Data Elasticsearch (외부 Elasticsearch 연결)
- **Auth/OAuth**: Spring Security OAuth2 Client
- **Infra/Deploy**: Docker, Docker Compose, Nginx, GitHub Actions, self-hosted runner, Docker Hub

### 아키텍처 개요

- **외부 요청 흐름**: Client → Nginx(80/443) → `geonpil`(8080)
- **서비스 간 통신**: `geonpil` → `search-service` (compose 네트워크 내부 통신)
- **데이터/검색 인프라**: `geonpil` ↔ MySQL(외부), `geonpil`/`search-service` ↔ Elasticsearch(외부)

운영 compose에는 다음 서비스가 포함됩니다.

- **`nginx`**: 80/443 리버스 프록시 → `geonpil:8080`
- **`geonpil`**: `${DOCKERHUB_USERNAME}/geonpil:latest`
- **`search-service`**: `${DOCKERHUB_USERNAME}/search-service:latest`

### CI/CD 요약

- **이미지 빌드/푸시**: GitHub-hosted runner에서 Docker Hub로 `latest` 및 `${{ github.sha }}` 태그 푸시
- **배포**: 배포 서버에 설치된 **self-hosted runner**가 `/opt/geonpil`의 `docker-compose.prod.yml` 기준으로 `pull`/`up -d` 수행

워크플로 파일은 아래에 있습니다.

- `geonpil` 배포 워크플로: `.github/workflows/deploy.yml`

### 문서

실행/배포 절차는 README에서 분리해 `docs/`에 정리했습니다.

- **운영 배포 가이드**: `docs/DEPLOY-PROD.md`
- **워크플로 설명**: `docs/workflow-deploy-explained.md`
