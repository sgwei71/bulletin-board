# Spring Boot 게시판 애플리케이션

Spring Boot를 사용한 레이어드 아키텍처 기반의 게시판 애플리케이션 템플릿입니다.

## 🚀 기술 스택

- **Framework**: Spring Boot 3.2.3
- **Language**: Java 17
- **Database**: H2 In-Memory DB
- **ORM/Mapper**: MyBatis 3.0.3
- **View**: Thymeleaf
- **UI Framework**: Bootstrap 5
- **Utilities**: Lombok, Jakarta Validation

## 📂 프로젝트 구조

```
bulletin-board/
├── pom.xml
├── README.md
├── .gitignore
├── src/main/
│   ├── java/com/example/board/
│   │   ├── BulletinBoardApplication.java      # Spring Boot 메인 클래스
│   │   ├── controller/
│   │   │   ├── HomeController.java             # 메인 라우팅
│   │   │   └── BoardController.java            # 게시판 관련 요청 처리
│   │   ├── service/
│   │   │   ├── BoardService.java               # 비즈니스 로직 인터페이스
│   │   │   └── BoardServiceImpl.java            # 비즈니스 로직 구현
│   │   ├── dao/
│   │   │   └── BoardMapper.java                # MyBatis Mapper 인터페이스
│   │   └── domain/
│   │       ├── Board.java                      # 게시물 엔티티/DTO
│   │       └── PageInfo.java                   # 페이지네이션 VO
│   └── resources/
│       ├── application.properties              # Spring Boot 설정
│       ├── schema.sql                          # DB 스키마
│       ├── data.sql                            # 샘플 데이터 (10건)
│       ├── mapper/
│       │   └── BoardMapper.xml                 # MyBatis SQL 매퍼
│       └── templates/board/
│           ├── list.html                       # 목록 페이지
│           ├── write.html                      # 등록/수정 폼 (공용)
│           └── view.html                       # 상세 조회 페이지
└── src/test/
    ├── java/com/example/board/
    │   ├── service/
    │   │   └── BoardServiceImplTest.java       # 서비스 계층 테스트 (10 테스트)
    │   ├── controller/
    │   │   └── BoardControllerTest.java        # 컨트롤러 계층 테스트 (18 테스트)
    │   └── domain/
    │       ├── BoardValidationTest.java        # Board 도메인 검증 테스트 (18 테스트)
    │       └── PageInfoTest.java               # PageInfo 페이지네이션 테스트 (10 테스트)
    └── resources/templates/board/              # 테스트용 템플릿
        ├── list.html
        ├── view.html
        └── write.html
```

## 🎯 핵심 기능

### 게시판 CRUD
| 기능 | HTTP Method | URL | 설명 |
|------|-----------|-----|------|
| 목록 조회 | GET | `/board/list` | 페이지네이션 포함 목록 조회 |
| 등록 폼 | GET | `/board/write` | 게시물 작성 폼 표시 |
| 등록 | POST | `/board/write` | 게시물 작성 처리 → 상세 페이지로 리다이렉트 |
| 상세 조회 | GET | `/board/{id}` | 게시물 상세 정보 조회 |
| 수정 폼 | GET | `/board/{id}/edit` | 게시물 수정 폼 표시 |
| 수정 | POST | `/board/{id}/edit` | 게시물 수정 처리 → 상세 페이지로 리다이렉트 |
| 삭제 | POST | `/board/{id}/delete` | 게시물 삭제 처리 → 목록 페이지로 리다이렉트 |

## 🏗️ 아키텍처 설계

### 레이어드 아키텍처
```
Controller (HTTP 요청 처리)
    ↓
Service (비즈니스 로직)
    ↓
DAO/Mapper (데이터 접근)
    ↓
Domain/Entity (데이터 모델)
```

### 핵심 설계 원칙

#### 1. PRG (Post-Redirect-Get) 패턴
- 폼 제출 후 리다이렉트로 브라우저 뒤로가기 버튼 중복 제출 방지
- 일관된 사용자 경험 제공

#### 2. 트랜잭션 관리 (@Transactional)
```java
@Service
@Transactional(readOnly = true)  // 클래스 레벨: 기본 읽기 전용
public class BoardServiceImpl {
    @Transactional(readOnly = false)  // 메서드 오버라이드: 쓰기 허용
    public void create(Board board) { ... }
}
```
- 읽기 쿼리 성능 최적화
- 쓰기 작업 명시적 표시

#### 3. 통합 DTO/Entity
- Board 클래스가 엔티티와 DTO 역할 수행
- 단순 구조, 낮은 복잡도

#### 4. MyBatis 재사용 블록 (`<sql>` id)
```xml
<!-- 재사용 가능한 컬럼 정의 -->
<sql id="boardColumns">...</sql>

<!-- 재사용 가능한 검색 조건 (향후 확장용) -->
<sql id="searchCondition">...</sql>
```

#### 5. PageInfo 캡슐화
- 페이지네이션 로직을 VO에 캡슐화
- offset, totalPage, hasPrev, hasNext 자동 계산
- 템플릿에서 간편한 페이지네이션 처리

## 🚀 실행 방법

### 필수 환경
- Java 17 이상
- Maven 3.6 이상

### 빌드 및 실행
```bash
cd /Users/sunggyuwi/work/dev/bulletin-board

# 빌드
mvn clean package

# 실행
mvn spring-boot:run
```

### 애플리케이션 접속
- **메인 페이지**: http://localhost:8080
- **게시판**: http://localhost:8080/board/list
- **H2 콘솔**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:boarddb`
  - Username: `sa`
  - Password: (empty)

## 🐳 Docker 빌드 및 실행

### 빠른 시작 (권장)
```bash
# 1. 이미지 빌드
docker build -t bulletin-board:1.0.0 .

# 2. 기존 컨테이너 정리 (필요시)
docker stop bulletin-board 2>/dev/null || true
docker rm bulletin-board 2>/dev/null || true

# 3. 백그라운드 실행 (데이터 영속성 포함)
docker run -d -p 8080:8080 \
  -v $(pwd)/data:/app/data \
  --name bulletin-board \
  bulletin-board:1.0.0

# 4. 로그 확인
docker logs -f bulletin-board
```

### 각 단계 설명

**1️⃣ Docker 이미지 빌드**
```bash
docker build -t bulletin-board:1.0.0 .
```

**2️⃣ Docker 컨테이너 실행**

포그라운드 실행 (로그 확인):
```bash
docker run -p 8080:8080 bulletin-board:1.0.0
```

백그라운드 실행 (권장) - 데이터 영속성 포함:
```bash
docker run -d -p 8080:8080 \
  -v $(pwd)/data:/app/data \
  --name bulletin-board \
  bulletin-board:1.0.0
```

> - `-d`: 백그라운드 실행
> - `-p 8080:8080`: 포트 매핑
> - `-v $(pwd)/data:/app/data`: 데이터 영속성 (재시작 후에도 데이터 유지)
> - `--name bulletin-board`: 컨테이너 이름 지정

### 데이터 영속성

Docker 재시작 후에도 데이터베이스 데이터를 유지하려면 **Docker 볼륨**을 사용합니다:

```bash
# 호스트의 ./data 디렉토리를 컨테이너의 /app/data와 매핑
docker run -d -p 8080:8080 \
  -v $(pwd)/data:/app/data \
  --name bulletin-board \
  bulletin-board:1.0.0
```

**참고**: 
- 샘플 데이터는 Docker 재시작할 때마다 초기화됩니다
- 사용자가 작성한 데이터는 `/app/data` 볼륨에 저장되어 유지됩니다

또는 명명된 볼륨 사용:
```bash
# 첫 실행 (볼륨 생성)
docker run -d -p 8080:8080 \
  -v bulletin-board-data:/app/data \
  --name bulletin-board \
  bulletin-board:1.0.0

# 나중에 재시작 시 (같은 볼륨 사용)
docker stop bulletin-board
docker start bulletin-board  # 데이터 유지됨!
```

**저장된 데이터 확인:**
```bash
# 로컬 디렉토리 방식
ls -la ./data/

# 명명된 볼륨 방식
docker volume ls | grep bulletin-board-data
```

> **주의**: 볼륨을 지정하지 않으면 컨테이너 삭제 시 모든 데이터가 사라집니다!

### Docker 명령어 참조

```bash
# 이미지 확인
docker images | grep bulletin-board

# 실행 중인 컨테이너 확인
docker ps

# 컨테이너 중지
docker stop <container_id>

# 컨테이너 삭제
docker rm <container_id>

# 이미지 삭제
docker rmi bulletin-board:1.0.0

# 컨테이너 로그 보기
docker logs <container_id>

# 실행 중인 컨테이너 진입
docker exec -it <container_id> /bin/sh
```

### Docker Compose 사용 (선택 사항)
`docker-compose.yml` 파일을 생성하여 더 편리하게 관리할 수 있습니다:

```yaml
version: '3.8'

services:
  bulletin-board:
    build: .
    ports:
      - "8080:8080"
    environment:
      - JAVA_OPTS=-Xmx256m -Xms128m
    container_name: bulletin-board-app
    volumes:
      - bulletin-board-data:/app/data
    restart: unless-stopped

volumes:
  bulletin-board-data:
    driver: local
```

실행:
```bash
# 백그라운드에서 실행
docker-compose up -d

# 중지
docker-compose down

# 로그 확인
docker-compose logs -f
```

## 📝 주요 코드 예시

### 1. Service 트랜잭션 관리
```java
@Service
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    // 읽기 메서드 - readOnly 트랜잭션 사용
    @Override
    public Board getById(Long boardId) {
        return boardMapper.getById(boardId);
    }

    // 쓰기 메서드 - readOnly=false로 오버라이드
    @Override
    @Transactional(readOnly = false)
    public void create(Board board) {
        boardMapper.insert(board);
    }
}
```

### 2. Controller PRG 패턴
```java
@PostMapping("/write")
public String writeProcess(@Valid Board board,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
        return "board/write";  // 검증 실패 시 폼으로
    }

    Long boardId = boardService.create(board);
    redirectAttributes.addFlashAttribute("message", "작성되었습니다.");

    return "redirect:/board/" + boardId;  // 성공 시 리다이렉트
}
```

### 3. MyBatis 동적 SQL
```xml
<sql id="searchCondition">
    <where>
        1 = 1
        <if test="searchType != null">
            <choose>
                <when test="searchType == 'title'">
                    AND TITLE LIKE CONCAT('%', #{searchKeyword}, '%')
                </when>
            </choose>
        </if>
    </where>
</sql>
```

### 4. PageInfo 페이지네이션
```java
PageInfo pageInfo = PageInfo.builder()
    .pageNum(pageNum)
    .pageSize(10)
    .totalCount(boardMapper.getTotalCount())
    .build();

pageInfo.calculatePageInfo();  // offset, startPage, endPage 등 자동 계산
```

## 🧪 테스트

### 자동화된 단위 테스트 (Unit Tests)

프로젝트에는 **56개의 포괄적인 테스트**가 포함되어 있습니다.

#### 1. BoardServiceImplTest (10 테스트)
서비스 계층의 비즈니스 로직 검증
- ✅ 게시물 목록 조회 (페이지네이션)
- ✅ null/음수 페이지 번호 처리
- ✅ 게시물 상세 조회 (존재/미존재)
- ✅ 게시물 생성, 수정, 삭제

```bash
mvn test -Dtest=BoardServiceImplTest
```

#### 2. BoardControllerTest (18 테스트)
컨트롤러 계층의 HTTP 요청 처리 검증 (@WebMvcTest)
- ✅ GET/POST 요청 처리
- ✅ 유효성 검증 실패 케이스
- ✅ PRG 패턴 (리다이렉트)
- ✅ 플래시 메시지 전달
- ✅ 존재하지 않는 리소스 처리 (404)

```bash
mvn test -Dtest=BoardControllerTest
```

#### 3. BoardValidationTest (18 테스트)
도메인 모델의 유효성 검증 (@Valid)
- ✅ 필수 필드 검증 (제목, 내용, 작성자)
- ✅ 길이 제한 검증
  - 제목: 2-200자
  - 내용: 5자 이상
  - 작성자: 2-50자
- ✅ 경계값 테스트 (min/max)

```bash
mvn test -Dtest=BoardValidationTest
```

#### 4. PageInfoTest (10 테스트)
페이지네이션 계산 로직 검증
- ✅ offset 계산 (LIMIT 쿼리용)
- ✅ 총 페이지 수 계산
- ✅ 페이지 네비게이션 블록 (5개씩)
- ✅ 이전/다음 버튼 존재 여부
- ✅ 다양한 페이지 크기 처리

```bash
mvn test -Dtest=PageInfoTest
```

### 테스트 실행 방법

```bash
# 전체 테스트 실행
mvn test

# 특정 테스트 클래스만 실행
mvn test -Dtest=BoardServiceImplTest

# 특정 테스트 메서드만 실행
mvn test -Dtest=BoardServiceImplTest#testCreate

# 테스트 리포트 보기
# 테스트 실행 후 target/surefire-reports 디렉토리 확인
```

### 수동 테스트 시나리오

1. **게시물 작성**
   - `/board/write` 접속
   - 제목, 작성자, 내용 입력
   - 등록 버튼 클릭
   - 상세 페이지로 리다이렉트 확인

2. **목록 조회 및 페이지네이션**
   - `/board/list` 접속
   - 10개 게시물 목록 표시
   - 페이지 네비게이션 동작 확인

3. **상세 조회**
   - 목록에서 게시물 선택
   - 제목, 작성자, 작성일, 내용 표시

4. **게시물 수정**
   - 상세 페이지에서 "수정하기" 버튼
   - 폼 수정 후 저장
   - 수정된 내용 반영 확인

5. **게시물 삭제**
   - 상세 페이지에서 "삭제하기" 버튼
   - 확인 메시지 표시
   - 목록으로 리다이렉트 확인

## 💡 학습 포인트

이 템플릿을 통해 배울 수 있는 내용:

1. **Spring Boot 기초**: 자동 설정, 내장 톰캣
2. **레이어드 아키텍처**: Controller-Service-DAO 계층 분리
3. **MyBatis**: SQL 맵퍼 프레임워크 사용법
4. **Thymeleaf**: 서버 템플릿 엔진 활용
5. **트랜잭션 관리**: @Transactional 선언적 관리
6. **유효성 검사**: Jakarta Validation (Jakarta Bean Validation)
7. **페이지네이션**: 효율적인 대용량 데이터 처리
8. **PRG 패턴**: 웹 애플리케이션 모범 사례
9. **단위 테스트**: JUnit 5, Mockito를 사용한 테스트 작성
10. **통합 테스트**: @WebMvcTest, MockMvc를 사용한 컨트롤러 테스트
11. **유효성 검증 테스트**: Jakarta Validation API 테스트
12. **테스트 커버리지**: 56개 테스트로 높은 코드 커버리지 달성

## 🔄 향후 확장 사항

1. **검색 기능**: MyBatis의 `<sql>` 블록 활용하여 검색 조건 추가
2. **정렬 기능**: 최신순, 인기순 등 정렬 옵션
3. **댓글 시스템**: 댓글 추가/삭제 기능
4. **파일 업로드**: 게시물에 파일 첨부 기능
5. **권한 관리**: Spring Security를 활용한 로그인/권한 시스템
6. **캐싱**: 조회 성능 최적화를 위한 캐시 레이어
7. **API 문서**: Swagger/OpenAPI를 사용한 REST API 문서화
8. **통합 테스트**: @SpringBootTest를 사용한 전체 애플리케이션 통합 테스트
9. **성능 테스트**: JMeter/K6을 사용한 부하 테스트
10. **CI/CD 파이프라인**: GitHub Actions/GitLab CI를 통한 자동화된 테스트 및 배포

## 📄 라이센스

이 프로젝트는 교육용 목적으로 제공됩니다.
