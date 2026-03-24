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
└── src/main/
    ├── java/com/example/board/
    │   ├── BulletinBoardApplication.java      # Spring Boot 메인 클래스
    │   ├── controller/
    │   │   ├── HomeController.java             # 메인 라우팅
    │   │   └── BoardController.java            # 게시판 관련 요청 처리
    │   ├── service/
    │   │   ├── BoardService.java               # 비즈니스 로직 인터페이스
    │   │   └── BoardServiceImpl.java            # 비즈니스 로직 구현
    │   ├── dao/
    │   │   └── BoardMapper.java                # MyBatis Mapper 인터페이스
    │   └── domain/
    │       ├── Board.java                      # 게시물 엔티티/DTO
    │       └── PageInfo.java                   # 페이지네이션 VO
    └── resources/
        ├── application.properties              # Spring Boot 설정
        ├── schema.sql                          # DB 스키마
        ├── data.sql                            # 샘플 데이터 (10건)
        ├── mapper/
        │   └── BoardMapper.xml                 # MyBatis SQL 매퍼
        └── templates/board/
            ├── list.html                       # 목록 페이지
            ├── write.html                      # 등록/수정 폼 (공용)
            └── view.html                       # 상세 조회 페이지
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

## 🧪 테스트 시나리오

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

## 🔄 향후 확장 사항

1. **검색 기능**: MyBatis의 `<sql>` 블록 활용하여 검색 조건 추가
2. **정렬 기능**: 최신순, 인기순 등 정렬 옵션
3. **댓글 시스템**: 댓글 추가/삭제 기능
4. **파일 업로드**: 게시물에 파일 첨부 기능
5. **권한 관리**: Spring Security를 활용한 로그인/권한 시스템
6. **캐싱**: 조회 성능 최적화를 위한 캐시 레이어
7. **API 문서**: Swagger/OpenAPI를 사용한 REST API 문서화

## 📄 라이센스

이 프로젝트는 교육용 목적으로 제공됩니다.
