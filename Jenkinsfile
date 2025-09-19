pipeline {
    agent any
    
    environment {
        DOCKER_DRIVER = 'overlay2'
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
        SERVICE_NAME = 'car-service'
        DOCKER_IMAGE_NAME = 'registry.gitlab.com/vroomie/car-service'
        GITLAB_CONFIG_REPO = 'https://gitlab.com/vroomie/vroomie-config.git'
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo "✅ Main repository checked out"
            }
        }
        
        stage('Fetch Environment Variables') {
            steps {
                script {
                    // Config 레포지토리 클론
                    dir('config-repo') {
                        git credentialsId: 'gitlab-config-credentials',
                            url: "${GITLAB_CONFIG_REPO}",
                            branch: 'main'
                    }
                    
                    // 브랜치별 환경변수 파일 선택
                    def envFile = ""
                    if (env.BRANCH_NAME == 'main') {
                        envFile = 'config-repo/.env.production'
                    } else if (env.BRANCH_NAME == 'develop') {
                        envFile = 'config-repo/.env.development'
                    } else {
                        envFile = 'config-repo/.env.test'
                    }
                    
                    echo "🔧 Loading environment variables from: ${envFile}"
                    
                    // 환경변수 로드
                    if (fileExists(envFile)) {
                        def envContent = readFile(envFile)
                        envContent.split('\n').each { line ->
                            line = line.trim()
                            if (line && !line.startsWith('#') && line.contains('=')) {
                                def parts = line.split('=', 2)
                                if (parts.length == 2) {
                                    def key = parts[0].trim()
                                    def value = parts[1].trim()
                                    env."${key}" = value
                                    echo "✅ Loaded: ${key}"
                                }
                            }
                        }
                    } else {
                        error "❌ Environment file not found: ${envFile}"
                    }
                }
            }
        }
        
        stage('Validate Environment') {
            steps {
                script {
                    def requiredVars = ['DB_URL', 'DB_USERNAME', 'DB_PASSWORD', 'SERVER_PORT', 'MYSQL_ROOT_PASSWORD']
                    def missingVars = []
                    
                    requiredVars.each { varName ->
                        if (!env."${varName}") {
                            missingVars.add(varName)
                        } else {
                            echo "✅ ${varName}: Available"
                        }
                    }
                    
                    if (missingVars) {
                        error "❌ Missing required environment variables: ${missingVars.join(', ')}"
                    }
                    
                    echo "🎯 All required environment variables are present"
                }
            }
        }
        
        stage('Test') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    changeRequest()
                }
            }
            
            steps {
                script {
                    echo "🧪 Starting test execution..."
                    
                    // MySQL 컨테이너 직접 실행 (docker-compose 없이)
                    sh '''
                        # 기존 테스트 MySQL 컨테이너 정리
                        docker stop test-mysql || true
                        docker rm test-mysql || true
                        
                        # MySQL 컨테이너 시작
                        docker run -d \
                            --name test-mysql \
                            -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} \
                            -e MYSQL_DATABASE=${MYSQL_DATABASE} \
                            -e MYSQL_USER=testuser \
                            -e MYSQL_PASSWORD=testpass \
                            -p 3306:3306 \
                            mysql:8.0 \
                            --default-authentication-plugin=mysql_native_password --skip-ssl
                        
                        echo "⏳ Waiting for MySQL to be ready..."
                        sleep 45
                        
                        # MySQL 연결 테스트
                        docker exec test-mysql mysqladmin ping -h localhost -u root -p${MYSQL_ROOT_PASSWORD} || echo "MySQL still starting..."
                        sleep 15
                    '''
                    
                    // 테스트 실행 - Jenkins 에이전트에서 직접 실행
                    sh '''
                        echo "🔨 Installing Java 17 on Jenkins agent..."
                        
                        # Java 17 설치 (이미 설치되어 있으면 스킵)
                        if ! java -version 2>&1 | grep -q "17"; then
                            apt-get update
                            apt-get install -y openjdk-17-jdk
                        fi
                        
                        # Java 17 환경 설정
                        export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
                        export PATH=$JAVA_HOME/bin:$PATH
                        
                        echo "☕ Java version check:"
                        java -version
                        
                        echo "🧪 Running tests..."
                        export SPRING_PROFILES_ACTIVE=test
                        export DB_URL="jdbc:mysql://localhost:3306/${MYSQL_DATABASE}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
                        export DB_USERNAME=testuser
                        export DB_PASSWORD=testpass
                        
                        chmod +x ./gradlew
                        ./gradlew clean test --info
                    '''
                    
                    echo "✅ Tests completed successfully"
                }
            }
            
            post {
                always {
                    echo "🧹 Cleaning up test environment..."
                    // MySQL 컨테이너 정리
                    sh '''
                        docker stop test-mysql || true
                        docker rm test-mysql || true
                    '''
                    
                    // 테스트 결과 수집 (선택사항 - 파일이 있을 때만)
                    script {
                        if (fileExists('build/test-results/test/*.xml')) {
                            junit 'build/test-results/test/*.xml'
                        }
                        
                        if (fileExists('build/reports/tests/test/index.html')) {
                            publishHTML([
                                allowMissing: false,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'build/reports/tests/test',
                                reportFiles: 'index.html',
                                reportName: 'Test Report'
                            ])
                        }
                    }
                }
            }
        }
        
        stage('Build') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    changeRequest()
                }
            }
            
            steps {
                script {
                    echo "🏗️ Starting build process..."
                    
                    // 이미지 태그 결정
                    def imageTag = ""
                    def environment = ""
                    
                    if (env.BRANCH_NAME == 'main') {
                        imageTag = "prod-${env.GIT_COMMIT[0..7]}"
                        environment = "prod"
                    } else if (env.BRANCH_NAME == 'develop') {
                        imageTag = "test-${env.GIT_COMMIT[0..7]}"
                        environment = "test"
                    } else {
                        imageTag = "branch-${env.GIT_COMMIT[0..7]}"
                        environment = "test"
                    }
                    
                    env.IMAGE_TAG = imageTag
                    env.ENVIRONMENT = environment
                    
                    echo "📦 Building image with tag: ${imageTag}"
                    
                    // Gradle 빌드
                    sh '''
                        echo "🔨 Building application with Gradle..."
                        chmod +x ./gradlew
                        ./gradlew clean build -x test
                        echo "✅ Gradle build completed"
                    '''
                    
                    // Docker 로그인 및 빌드
                    withCredentials([usernamePassword(credentialsId: 'docker-registry-credentials', 
                                                    usernameVariable: 'DOCKER_USER', 
                                                    passwordVariable: 'DOCKER_PASS')]) {
                        sh '''
                            echo "🔐 Logging into Docker registry..."
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin registry.gitlab.com
                            
                            echo "🐳 Building Docker image..."
                            docker build -t "$DOCKER_IMAGE_NAME:$IMAGE_TAG" .
                            
                            echo "📤 Pushing Docker image..."
                            docker push "$DOCKER_IMAGE_NAME:$IMAGE_TAG"
                            
                            echo "✅ Docker image pushed: $DOCKER_IMAGE_NAME:$IMAGE_TAG"
                        '''
                        
                        // main 브랜치인 경우 latest 태그도 푸시
                        if (env.BRANCH_NAME == 'main') {
                            sh '''
                                echo "🏷️ Tagging as latest..."
                                docker tag "$DOCKER_IMAGE_NAME:$IMAGE_TAG" "$DOCKER_IMAGE_NAME:latest"
                                docker push "$DOCKER_IMAGE_NAME:latest"
                                echo "✅ Latest tag pushed"
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            
            steps {
                script {
                    input message: "Production에 배포하시겠습니까?", ok: "Deploy"

                    echo "🚀 Starting production deployment..."
                    echo "👤 Deployed by: ${env.DEPLOYER}"
                    
                    sshagent(credentials: ['prod-server-ssh-key']) {
                        withCredentials([usernamePassword(credentialsId: 'docker-registry-credentials', 
                                                        usernameVariable: 'DOCKER_USER', 
                                                        passwordVariable: 'DOCKER_PASS')]) {
                            sh '''
                                ssh -o ConnectTimeout=10 -o StrictHostKeyChecking=no ubuntu@193.123.232.148 << 'EOF'
                                    echo "🏠 Starting deployment on production server..."
                                    cd /home/ubuntu
                                    
                                    # 기존 디렉토리 정리
                                    if [ -d "car-service" ]; then
                                        echo "🧹 Cleaning up existing directory..."
                                        sudo chown -R ubuntu:ubuntu car-service || true
                                        rm -rf car-service || true
                                    fi
                                    
                                    # 소스코드 클론
                                    echo "📥 Cloning source code..."
                                    git clone https://gitlab.com/vroomie/car-service.git
                                    cd car-service
                                    
                                    # 환경변수 파일 생성
                                    echo "⚙️ Creating environment file..."
                                    cat > .env << 'ENVEOF'
DB_URL=''' + env.DB_URL + '''
DB_USERNAME=''' + env.DB_USERNAME + '''
DB_PASSWORD=''' + env.DB_PASSWORD + '''
MYSQL_ROOT_PASSWORD=''' + env.MYSQL_ROOT_PASSWORD + '''
MYSQL_DATABASE=''' + env.MYSQL_DATABASE + '''
SERVER_PORT=''' + env.SERVER_PORT + '''
JAVA_OPTS=''' + env.JAVA_OPTS + '''
ENVEOF
                                    
                                    # 기존 컨테이너 정리
                                    echo "🛑 Stopping existing containers..."
                                    docker-compose -f docker-compose.prod.yml down -v --remove-orphans || true
                                    docker ps -a | grep -E "(car-service|mysql)" | awk '{print $1}' | xargs -r docker rm -f || true
                                    docker volume rm car-service_mysql_prod_data || true
                                    docker system prune -f || true
                                    
                                    # Docker 로그인 및 이미지 pull
                                    echo "🔐 Logging into Docker registry..."
                                    echo "''' + env.DOCKER_PASS + '''" | docker login -u "''' + env.DOCKER_USER + '''" --password-stdin registry.gitlab.com
                                    
                                    echo "📥 Pulling latest image..."
                                    docker pull ''' + env.DOCKER_IMAGE_NAME + ':' + env.IMAGE_TAG + '''
                                    docker tag ''' + env.DOCKER_IMAGE_NAME + ':' + env.IMAGE_TAG + ''' car-service:latest
                                    
                                    # 서비스 시작
                                    echo "🚀 Starting services..."
                                    docker-compose -f docker-compose.prod.yml up -d
                                    
                                    # 서비스 상태 확인
                                    echo "⏳ Waiting for services to be ready..."
                                    sleep 20
                                    
                                    echo "📊 Service status:"
                                    docker-compose -f docker-compose.prod.yml ps -a
                                    
                                    echo "✅ Deployment completed successfully!"
EOF
                            '''
                        }
                    }
                    
                    echo "🎉 Production deployment completed!"
                }
            }
        }
    }
    
    post {
        always {
            echo "🧹 Cleaning up workspace..."
            // Config 레포지토리 정리
            sh 'rm -rf config-repo'
            
            // Docker 이미지 정리 (로컬에서만)
            sh 'docker system prune -f || true'
        }
        
        success {
            echo '🎉 Pipeline completed successfully!'
            echo "✅ Branch: ${env.BRANCH_NAME}"
            echo "✅ Environment: ${env.ENVIRONMENT ?: 'N/A'}"
            echo "✅ Image Tag: ${env.IMAGE_TAG ?: 'N/A'}"
        }
        
        failure {
            echo '❌ Pipeline execution failed!'
            echo "💥 Check the logs above for details"
        }
        
        unstable {
            echo '⚠️ Pipeline execution was unstable'
        }
    }
}