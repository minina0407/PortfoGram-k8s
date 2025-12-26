# LocalPortfoGram

LocalPortfoGram은 포트폴리오 공유 플랫폼입니다. 사용자들이 자신의 프로젝트와 작품을 공유하고, 다른 사용자들과 소통할 수 있는 소셜 미디어 스타일의 웹 애플리케이션입니다.

## 🚀 기술 스택

### Frontend

- **React 18** - 사용자 인터페이스 구축
- **TypeScript** - 타입 안전성 보장
- **Styled Components** - CSS-in-JS 스타일링
- **React Router** - 클라이언트 사이드 라우팅
- **React Query** - 서버 상태 관리
- **React Hook Form** - 폼 관리
- **Framer Motion** - 애니메이션
- **Axios** - HTTP 클라이언트

### Backend

- **Spring Boot 3** - RESTful API 서버
- **Spring Security** - 인증 및 권한 관리
- **Spring Data JPA** - 데이터 접근 계층
- **MySQL** - 관계형 데이터베이스
- **Redis** - 캐싱 및 세션 관리
- **JWT** - 토큰 기반 인증

### DevOps & Monitoring

- **Docker** - 컨테이너화
- **Nginx** - 리버스 프록시 및 정적 파일 서빙
- **Prometheus** - 메트릭 수집
- **Grafana** - 모니터링 대시보드
- **Loki** - 로그 수집
- **OpenTelemetry** - 분산 추적
- **ArgoCD** - GitOps 배포
- **GitHub Actions** - CI/CD

## 📁 프로젝트 구조

```
localportfogram/
├── frontend/                 # React 프론트엔드
│   ├── public/              # 정적 파일
│   ├── src/                 # 소스 코드
│   │   ├── api/            # API 클라이언트
│   │   ├── components/     # React 컴포넌트
│   │   ├── contexts/       # React Context
│   │   ├── pages/          # 페이지 컴포넌트
│   │   ├── styles/         # 글로벌 스타일
│   │   └── types/          # TypeScript 타입 정의
│   ├── package.json        # 프론트엔드 의존성
│   └── tsconfig.json       # TypeScript 설정
├── spring-boot-app/         # Spring Boot 백엔드
│   ├── src/main/java/      # Java 소스 코드
│   ├── src/main/resources/ # 설정 파일
│   └── build.gradle        # Gradle 빌드 설정
├── Dockerfile              # 멀티스테이지 Docker 빌드
├── nginx.conf              # Nginx 설정
├── .dockerignore           # Docker 빌드 제외 파일
└── README.md               # 프로젝트 문서
```

## 🛠️ 개발 환경 설정

### Prerequisites

- Node.js 18+
- Java 17+
- Docker & Docker Compose
- MySQL 8.0+
- Redis 6.0+

### 로컬 개발 환경

#### 1. 프론트엔드 실행

```bash
cd frontend
npm install
npm start
```

프론트엔드는 `http://localhost:3000`에서 실행됩니다.

#### 2. 백엔드 실행

```bash
cd spring-boot-app
./gradlew bootRun
```

백엔드는 `http://localhost:8080`에서 실행됩니다.

### Docker를 사용한 실행

#### 개발 환경

```bash
# 프론트엔드만 실행
docker-compose up frontend

# 백엔드만 실행
docker-compose up backend

# 전체 애플리케이션 실행
docker-compose up
```

#### 프로덕션 빌드

```bash
# 전체 애플리케이션 빌드 및 실행
docker build -t localportfogram .
docker run -p 80:80 -p 8080:8080 localportfogram
```

## 📡 API 엔드포인트

### 인증 (Authentication)

- `POST /api/v1/auth/login` - 로그인
- `POST /api/v1/auth/reissue` - 토큰 재발급

### 사용자 (User)

- `GET /api/v1/users/profile` - 프로필 조회
- `POST /api/v1/users/follow` - 사용자 팔로우
- `POST /api/v1/users/unfollow` - 사용자 언팔로우
- `POST /api/v1/users` - 회원가입
- `DELETE /api/v1/users/withdrawal` - 회원탈퇴

### 포트폴리오 (Portfolio)

- `GET /api/v1/portfolios/{id}` - 포트폴리오 조회
- `GET /api/v1/portfolios` - 포트폴리오 목록 조회
- `POST /api/v1/portfolios` - 포트폴리오 생성
- `PUT /api/v1/portfolios/{id}` - 포트폴리오 수정
- `DELETE /api/v1/portfolios/{id}` - 포트폴리오 삭제
- `POST /api/v1/portfolios/{id}/likes` - 좋아요
- `POST /api/v1/portfolios/{id}/bookmarks` - 북마크
- `GET /api/v1/portfolios/{id}/comments` - 댓글 조회

### 댓글 (Comment)

- `GET /api/v1/comments/{id}/replies` - 답글 조회
- `PUT /api/v1/comments/{id}` - 댓글 수정
- `DELETE /api/v1/comments/{id}` - 댓글 삭제

### 답글 (Reply)

- `POST /api/v1/replies` - 답글 생성
- `PUT /api/v1/replies/{id}` - 답글 수정
- `DELETE /api/v1/replies/{id}` - 답글 삭제

### 채팅 (Chat)

- `POST /api/chat/rooms` - 채팅방 생성
- `POST /api/chat/rooms/{id}/join` - 채팅방 입장
- `POST /api/chat/rooms/{id}/messages` - 메시지 전송
- `GET /api/chat/rooms/{id}/messages` - 메시지 조회

### 이미지 (Image)

- `POST /api/v1/image` - 이미지 업로드
- `GET /api/v1/portfolios/images/{id}` - 이미지 조회

## 🎨 주요 기능

### 사용자 기능

- ✅ 회원가입 및 로그인
- ✅ 프로필 관리
- ✅ 사용자 팔로우/언팔로우
- ✅ 회원탈퇴

### 포트폴리오 기능

- ✅ 포트폴리오 CRUD
- ✅ 이미지 업로드
- ✅ 좋아요/북마크
- ✅ 댓글 및 답글
- ✅ 무한 스크롤

### 채팅 기능

- ✅ 실시간 채팅
- ✅ 채팅방 생성 및 입장
- ✅ 메시지 전송 및 조회

### UI/UX 기능

- ✅ 반응형 디자인
- ✅ 다크/라이트 모드
- ✅ 부드러운 애니메이션
- ✅ 로딩 상태 표시
- ✅ 에러 처리

## 🔧 개발 가이드

### 프론트엔드 개발

```bash
# 개발 서버 실행
npm start

# 빌드
npm run build

# 테스트
npm test

# 린트 검사
npm run lint
```

### 백엔드 개발

```bash
# 개발 서버 실행
./gradlew bootRun

# 빌드
./gradlew build

# 테스트
./gradlew test
```

### 코드 스타일

- 프론트엔드: ESLint + Prettier
- 백엔드: Checkstyle

## 🚀 배포

### Docker 배포

```bash
# 이미지 빌드
docker build -t localportfogram .

# 컨테이너 실행
docker run -d \
  -p 80:80 \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/localportfogram \
  -e SPRING_DATASOURCE_USERNAME=your_username \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  localportfogram
```

### Kubernetes 배포

```bash
# 네임스페이스 생성
kubectl create namespace localportfogram

# 배포
kubectl apply -f k8s/
```

## 📊 모니터링

### 메트릭 수집

- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3001`

### 로그 수집

- Loki: `http://localhost:3100`

### API 문서

- Swagger UI: `http://localhost:8080/swagger-ui/`

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.

---

**LocalPortfoGram** - 포트폴리오 공유의 새로운 경험을 만들어갑니다! 🎨✨

