#!/bin/bash

# Car Service 로컬 개발 환경 배포 스크립트
# GitLab CI/CD 마이그레이션 후 로컬 개발용으로만 사용
# 사용법: ./deploy.sh [dev|local] [build]

set -e  # 에러 발생시 스크립트 중단

# 색깔 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 설정 폴더 경로
CONFIG_DIR="../vroomie-config"
ENV_FILE="$CONFIG_DIR/.env.development"
ENV_EXAMPLE_FILE="$CONFIG_DIR/.env.development"

# 로그 함수
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# 도움말 출력
show_help() {
    echo "Car Service 로컬 개발 환경 배포 스크립트"
    echo ""
    echo "⚠️  주의: 이 스크립트는 로컬 개발 환경용입니다."
    echo "    테스트/운영 환경은 GitLab CI/CD를 사용하세요."
    echo ""
    echo "📁 설정 파일 경로: $CONFIG_DIR/"
    echo ""
    echo "사용법:"
    echo "  ./deploy.sh [환경] [옵션]"
    echo ""
    echo "환경:"
    echo "  dev|local - 로컬 개발 환경 (포트: 8080)"
    echo ""
    echo "옵션:"
    echo "  build     - Docker 이미지 강제 빌드"
    echo ""
    echo "예시:"
    echo "  ./deploy.sh dev"
    echo "  ./deploy.sh local build"
    echo ""
    echo "GitLab CI/CD 사용법:"
    echo "  - develop 브랜치: 테스트 환경 자동 배포"
    echo "  - main 브랜치: 운영 환경 수동 배포"
    echo "  - 자세한 내용: docs/gitlab-cicd-setup.md 참고"
}

# 파라미터 확인
ENVIRONMENT=$1
BUILD_OPTION=$2

if [ -z "$ENVIRONMENT" ]; then
    show_help
    exit 1
fi

# 환경 검증
case $ENVIRONMENT in
    dev|local)
        log_info "로컬 개발 환경으로 배포를 시작합니다."
        ENVIRONMENT="dev"  # 통일성을 위해 dev로 설정
        ;;
    test|prod|dual)
        log_error "❌ $ENVIRONMENT 환경은 GitLab CI/CD에서만 지원됩니다."
        echo ""
        echo "GitLab CI/CD 사용 방법:"
        echo "  1. 코드를 develop 브랜치에 푸시 → 테스트 환경 자동 배포"
        echo "  2. 코드를 main 브랜치에 푸시 → 운영 환경 수동 배포"
        echo "  3. 자세한 설정: docs/gitlab-cicd-setup.md 참고"
        echo ""
        echo "로컬 개발환경만 사용하려면: ./deploy.sh dev"
        exit 1
        ;;
    *)
        log_error "지원하지 않는 환경입니다: $ENVIRONMENT"
        show_help
        exit 1
        ;;
esac

# vroomie-config 폴더 확인
if [ ! -d "$CONFIG_DIR" ]; then
    log_error "설정 폴더를 찾을 수 없습니다: $CONFIG_DIR"
    log_info "vroomie-config 폴더가 상위 디렉토리에 있는지 확인하세요."
    exit 1
fi

# 로컬 개발 환경 설정 파일 확인
if [ ! -f "$ENV_FILE" ]; then
    if [ -f "$ENV_EXAMPLE_FILE" ]; then
        log_warning ".env 파일이 없습니다. .env.development 복사합니다."
        cp "$ENV_EXAMPLE_FILE" "$ENV_FILE"
        log_info "$ENV_FILE 파일을 확인하고 필요시 수정하세요."
    else
        log_error ".env 파일과 .env.development 파일이 모두 없습니다."
        log_info "개발 환경을 위한 .env 파일을 $CONFIG_DIR 폴더에 생성하세요."
        exit 1
    fi
fi
log_info "설정 파일 로드: $ENV_FILE"

# .env 파일을 현재 디렉토리에 심볼릭 링크로 연결 (Docker Compose가 인식할 수 있도록)
if [ -L ".env" ]; then
    rm ".env"
fi
ln -sf "$ENV_FILE" ".env"
log_info "환경 설정 파일 연결 완료"

# Gradle 빌드
log_info "애플리케이션 빌드 중..."sudo service docker restart
if command -v ./gradlew &> /dev/null; then
    ./gradlew clean build -x test
elif command -v gradle &> /dev/null; then
    gradle clean build -x test
else
    log_error "Gradle을 찾을 수 없습니다."
    exit 1
fi

# JAR 파일 확인
JAR_FILE=$(ls build/libs/car-service-*.jar 2>/dev/null | head -1)
if [ -z "$JAR_FILE" ]; then
    log_error "빌드된 JAR 파일을 찾을 수 없습니다."
    log_info "Gradle 빌드가 성공했는지 확인하세요: ./gradlew build"
    exit 1
fi
log_info "JAR 파일 확인: $JAR_FILE"

# Docker Compose 파일 선택 (로컬 개발 환경만)
COMPOSE_FILES="-f docker-compose.yml"
log_info "로컬 개발 환경용 docker-compose.yml 사용"

# 기존 컨테이너 중지
log_info "기존 컨테이너를 중지합니다..."
docker-compose $COMPOSE_FILES down

# Docker 이미지 빌드 옵션
BUILD_FLAG=""
if [ "$BUILD_OPTION" = "build" ]; then
    BUILD_FLAG="--build"
    log_info "Docker 이미지를 새로 빌드합니다..."
fi

# 컨테이너 실행
log_info "$ENVIRONMENT 환경 컨테이너를 실행합니다..."
docker-compose $COMPOSE_FILES up -d $BUILD_FLAG

# 헬스체크 대기
log_info "서비스 시작을 기다리는 중..."
sleep 30

# 서비스 상태 확인
log_info "서비스 상태를 확인합니다..."
docker-compose $COMPOSE_FILES ps

# 헬스체크
PORT=8081

log_success "🎉 로컬 개발 환경 배포가 완료되었습니다!"
echo ""
echo "📋 접속 정보:"
echo "  - 헬스체크: http://localhost:$PORT/api/actuator/health"
echo "  - Swagger UI: http://localhost:$PORT/api/swagger-ui.html"
echo "  - MySQL: localhost:3306"
echo ""
echo "🔧 유용한 명령어:"
echo "  - 로그 확인: docker-compose logs -f car-service"
echo "  - 컨테이너 상태: docker-compose ps"
echo "  - 컨테이너 중지: docker-compose down"
echo "  - 설정 파일 편집: vi $ENV_FILE"
echo ""
echo "⚠️  테스트/운영 환경 배포는 GitLab CI/CD를 사용하세요!"
echo "   자세한 내용: docs/gitlab-cicd-setup.md"