# CLAUDE.md

이 파일은 Claude Code (claude.ai/code)가 이 리포지토리에서 작업할 때 참고하는 가이드입니다.

## 언어 및 커뮤니케이션 규칙

### 기본 응답 언어
- **모든 응답**: 한국어로 작성
- **대화 및 설명**: 한국어 사용

### 코드 작성 규칙
- **코드 주석**: 한국어로 작성
- **변수명/함수명**: 영어 (코드 표준 준수)
- **타입/클래스명**: 영어

### 문서화 및 커밋
- **커밋 메시지**: 한국어로 작성 (형식: `[타입] 설명`)
- **PR 제목/설명**: 한국어로 작성
- **문서 파일**: 한국어로 작성
- **코드 예시/스니펫**: 필요시 설명은 한국어, 코드는 표준 영어 사용

## 빌드 및 실행 명령어

### 개발 환경
```bash
# 프로젝트 빌드
mvn clean package

# 애플리케이션 실행
mvn spring-boot:run

# 특정 포트로 실행
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### 테스트
```bash
# 전체 테스트 실행
mvn test

# 특정 테스트 클래스만 실행
mvn test -Dtest=BoardServiceImplTest

# 특정 테스트 메서드만 실행
mvn test -Dtest=BoardServiceImplTest#testCreate

# 커버리지 리포트 생성
mvn test jacoco:report
```

### Docker
```bash
# Docker 이미지 빌드
docker build -t bulletin-board:1.0.0 .

# 데이터 영속성을 포함하여 컨테이너 실행
docker run -d -p 8080:8080 \
  -v $(pwd)/data:/app/data \
  --name bulletin-board \
  bulletin-board:1.0.0
```

### 애플리케이션 접속
- **메인 페이지**: http://localhost:8080
- **게시판 목록**: http://localhost:8080/board/list
- **H2 콘솔**: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:boarddb`, 사용자: `sa`, 비밀번호: 없음)

## 아키텍처 개요

이 프로젝트는 **Spring Boot 계층형 아키텍처**로 구성되어 있습니다:

```
HTTP 요청 → Controller → Service → DAO/Mapper → 데이터베이스
                                  ↓
                            Domain Model
```

### 주요 컴포넌트

**Controllers** (`com.example.board.controller`)
- HTTP 요청을 처리하고 사용자 상호작용 관리
- `@Valid`와 `BindingResult`를 사용한 유효성 검증
- PRG(Post-Redirect-Get) 패턴으로 중복 제출 방지
- 서비스 계층에 데이터를 전달하고 Thymeleaf 템플릿 반환

**Service Layer** (`com.example.board.service`)
- 비즈니스 로직 구현
- 기본적으로 `@Transactional(readOnly = true)` 사용 (읽기 전용)
- CREATE/UPDATE/DELETE 작업은 `@Transactional(readOnly = false)` 오버라이드
- 페이지네이션 로직 처리 및 페이지 정보 계산

**Data Access** (`com.example.board.dao`)
- MyBatis mapper 인터페이스 `BoardMapper`가 SQL 작업 정의
- Mapper XML 파일 (`resources/mapper/BoardMapper.xml`)이 SQL 쿼리 포함
- `<sql>` 블록을 사용한 동적 SQL 재사용 지원 (검색 기능 확장 준비)

**Domain Models** (`com.example.board.domain`)
- `Board`: 게시판 게시물의 통합 entity/DTO
- `PageInfo`: 페이지네이션 로직 캡슐화, offset/페이지 번호/네비게이션 플래그 자동 계산

## 핵심 아키텍처 패턴

### 1. 트랜잭션 관리 전략
- **클래스 레벨 기본값**: 서비스 클래스에 `@Transactional(readOnly = true)` 적용
- **메서드 오버라이드**: 데이터 변경 작업은 `@Transactional(readOnly = false)` 사용
- **이유**: 읽기 쿼리 성능 최적화 및 쓰기 작업을 명시적으로 표시

예제:
```java
@Service
@Transactional(readOnly = true)  // 기본값: 모든 메서드가 읽기 전용
public class BoardServiceImpl {
    @Transactional(readOnly = false)  // 오버라이드: 쓰기 허용
    public void create(Board board) { ... }
}
```

### 2. PRG(Post-Redirect-Get) 패턴
- POST 요청 후 리다이렉트하여 페이지 새로고침 시 중복 제출 방지
- `RedirectAttributes`를 통해 플래시 메시지 전달
- 패턴: POST → 서비스 호출 → GET 엔드포인트로 리다이렉트

### 3. 페이지네이션 아키텍처
- `PageInfo` VO가 페이지네이션 로직을 캡슐화
- `offset` (SQL의 LIMIT용), `startPage`, `endPage`, `hasPrev`, `hasNext` 자동 계산
- 서비스 계층에서 페이지 번호 검증 (유효하지 않으면 1로 기본값 설정)

## MyBatis 관련 패턴

### 재사용 가능한 SQL 블록
Mapper XML은 자주 사용되는 부분을 `<sql>` 블록으로 정의:
- `boardColumns`: 표준 SELECT 컬럼
- `searchCondition`: WHERE 절 조건 (향후 검색 기능 확장을 위해 준비됨)

검색 기능을 추가할 때는 WHERE 로직을 중복하는 대신 `searchCondition`을 확장합니다.

### Mapper 쿼리
`BoardMapper.xml`의 주요 쿼리:
- `getList`: offset/limit을 포함한 페이지네이션 목록
- `getTotalCount`: 페이지네이션 계산용 전체 개수
- `getById`: 단일 레코드 조회
- `insert`, `update`, `delete`: 데이터 변경 작업

## 테스트 구조

총 56개의 테스트가 계층별로 구성되어 있습니다:

| 테스트 클래스 | 계층 | 개수 | 주요 초점 |
|------------|-------|-------|-----------|
| `BoardServiceImplTest` | Service | 10 | 비즈니스 로직, 페이지네이션, null 처리 |
| `BoardControllerTest` | Controller | 18 | 요청 처리, 검증, 리다이렉트, 404 |
| `BoardValidationTest` | Domain | 18 | 필드 검증 (길이, 필수 여부, 경계값) |
| `PageInfoTest` | Domain | 10 | 페이지네이션 계산, 엣지 케이스 |

**중요**: 컨트롤러 테스트는 `@WebMvcTest` 사용 (서비스는 mocking), 서비스 테스트는 `@SpringBootTest` 또는 mock을 포함한 순수 단위 테스트 사용. 검증 테스트는 모델 객체에 직접 `@Valid` 제약 조건을 확인합니다.

## 파일 구조 규칙

```
src/main/java/com/example/board/
├── BulletinBoardApplication.java    # @SpringBootApplication
├── controller/                       # HTTP 요청 처리
├── service/                          # 비즈니스 로직 (인터페이스 + 구현)
├── dao/                              # MyBatis mapper 인터페이스
└── domain/                           # 모델 (Board, PageInfo)

src/main/resources/
├── application.properties            # Spring 설정
├── schema.sql, data.sql              # H2 초기화 스크립트
├── mapper/BoardMapper.xml            # MyBatis 쿼리
└── templates/board/                  # Thymeleaf 뷰 (목록, 작성, 조회)

src/test/java/com/example/board/
├── service/                          # 서비스 계층 테스트
├── controller/                       # 컨트롤러 계층 테스트
└── domain/                           # 도메인 검증 및 페이지네이션 테스트
```

## 구현 노트

### 기능 추가 시

1. **검색/필터**: Mapper XML에서 WHERE 로직을 중복하는 대신 `<sql id="searchCondition">`을 확장합니다. 해당 메서드를 `BoardMapper` 인터페이스와 서비스에 추가합니다.

2. **새 REST 엔드포인트**: 컨트롤러에 메서드를 추가하고, 서비스 로직을 구현하며, 데이터베이스 접근이 필요하면 mapper 메서드를 생성합니다. 입력 검증은 `@Valid`를 사용합니다.

3. **데이터베이스 변경**: `schema.sql`과 `data.sql` (테스트 데이터) 모두 업데이트합니다. 페이지네이션 offset 계산이 새 데이터를 고려하는지 확인합니다.

4. **테스트**: 기존 패턴을 따릅니다. 서비스 레벨에서 의존성을 mocking하고, 컨트롤러는 `@WebMvcTest`를 사용하며, 도메인 검증은 독립적으로 테스트합니다.

## 일반적인 개발 워크플로우

**Board에 필드 추가**:
1. `Board.java`를 Lombok 주석과 검증 제약 조건으로 업데이트
2. `schema.sql` 테이블 정의 업데이트
3. `data.sql` 샘플 데이터 업데이트
4. MyBatis mapper SELECT/INSERT 컬럼 업데이트
5. 관련 뷰(list.html, view.html, write.html) 업데이트
6. `BoardValidationTest.java`에 검증 테스트 추가

**개발 중 특정 테스트만 실행**:
```bash
mvn test -Dtest=BoardServiceImplTest#testGetListWithPage
```

**테스트 커버리지 확인**:
기존 56개의 테스트는 CRUD, 검증 및 페이지네이션 로직에 대한 견고한 기본 커버리지를 제공합니다.

## NKS 배포 파이프라인

### 아키텍처 개요
```
GitHub (main branch push)
    ↓
GitHub Actions Workflow
    ├─ Maven 빌드
    ├─ Docker 이미지 빌드
    └─ Docker Hub에 푸시
    ↓
NKS Cluster (namespace: edu)
    ├─ ConfigMap 배포
    ├─ Deployment 배포 (2 replicas)
    └─ LoadBalancer Service 배포
```

### 사전 준비사항

1. **GitHub Secrets 설정** (저장소 Settings → Secrets and variables)
   - `DOCKER_HUB_USERNAME`: Docker Hub 사용자명
   - `DOCKER_HUB_TOKEN`: Docker Hub Personal Access Token
   - `KUBE_CONFIG`: NKS kubeconfig 파일을 base64로 인코딩한 값
   
   kubeconfig 인코딩:
   ```bash
   cat ~/.kube/config | base64 -w 0
   ```

2. **Docker Hub 준비**
   - Docker Hub 계정 생성
   - Personal Access Token 생성 (Settings → Security)
   - `bulletin-board` 레포지토리 생성 (공개 또는 비공개)

3. **NKS 클러스터 준비**
   ```bash
   # edu namespace 생성
   kubectl create namespace edu
   
   # 클러스터 확인
   kubectl cluster-info
   kubectl get nodes -A
   
   # 현재 context 확인
   kubectl config current-context
   ```

### 배포 파일 구조
```
k8s/
├── deployment.yaml       # 애플리케이션 배포 설정 (2 replicas)
├── service.yaml          # LoadBalancer 서비스
└── configmap.yaml        # 애플리케이션 설정

.github/
└── workflows/
    └── deploy.yml        # GitHub Actions 파이프라인
```

### 배포 워크플로우

1. **수동 배포** (GitHub Actions에서 Run workflow)
   ```bash
   # main 브랜치에 푸시
   git push origin main
   ```
   → GitHub Actions가 자동으로 실행되어 Docker 이미지 빌드 및 NKS 배포

2. **배포 상태 확인**
   ```bash
   # Pod 상태 확인
   kubectl get pods -n edu -l app=bulletin-board
   
   # Deployment 상태 확인
   kubectl get deployment bulletin-board -n edu
   
   # Service 상태 및 외부 IP 확인
   kubectl get svc bulletin-board -n edu
   
   # 로그 확인
   kubectl logs -n edu -l app=bulletin-board -f
   ```

3. **애플리케이션 접근**
   ```bash
   # LoadBalancer 외부 IP 획득 (약 1-2분 소요)
   kubectl get svc bulletin-board -n edu -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
   
   # 브라우저에서 접근
   http://<EXTERNAL-IP>
   ```

### 주요 설정값

**Deployment**
- Replicas: 2 (고가용성)
- CPU 요청: 100m, 제한: 500m
- 메모리 요청: 256Mi, 제한: 512Mi
- Liveness Probe: 30초 초기 딜레이, 10초 주기
- Readiness Probe: 10초 초기 딜레이, 5초 주기

**Service**
- Type: LoadBalancer (NKS 공개 IP 자동 할당)
- Port: 80 (외부)
- TargetPort: 8080 (컨테이너)

### 트러블슈팅

**Pod이 CrashLoopBackOff 상태인 경우**
```bash
kubectl logs -n edu <pod-name>
kubectl describe pod -n edu <pod-name>
```

**LoadBalancer IP가 Pending 상태인 경우**
- NKS 클러스터의 Load Balancer가 프로비저닝되는 동안 대기 (1-5분)
- `kubectl get svc -n edu -w` 로 실시간 모니터링

**이미지 풀 실패 (ImagePullBackOff)**
- Docker Hub 레포지토리가 비공개인 경우 시크릿 생성 필요
  ```bash
  kubectl create secret docker-registry dockerhub-secret \
    --docker-server=docker.io \
    --docker-username=YOUR_USERNAME \
    --docker-password=YOUR_TOKEN \
    -n edu
  ```
- deployment.yaml에 `imagePullSecrets` 추가

### 배포 버전 관리

GitHub Actions에서 각 push마다 다음과 같이 태그됩니다:
- `latest`: 가장 최신 커밋
- `<commit-sha>`: 특정 커밋 버전

필요 시 특정 버전으로 롤백:
```bash
kubectl set image deployment/bulletin-board \
  bulletin-board=YOUR_USERNAME/bulletin-board:COMMIT_SHA \
  -n edu
```

### 로컬 테스트 (배포 전)

```bash
# 로컬에서 kubectl 테스트
kubectl apply -f k8s/configmap.yaml --dry-run=client -o yaml
kubectl apply -f k8s/deployment.yaml --dry-run=client -o yaml

# 실제 적용 (로컬 클러스터 또는 테스트 환경)
kubectl apply -f k8s/
kubectl rollout status deployment/bulletin-board -n edu --timeout=5m
```
