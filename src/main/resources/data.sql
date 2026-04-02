INSERT INTO BOARD (TITLE, CONTENT, AUTHOR, CREATED_DATE, VIEW_COUNT)
SELECT * FROM (
    SELECT 'Spring Boot 기초 강좌', 'Spring Boot는 Java 기반의 웹 애플리케이션 개발을 쉽게 해주는 프레임워크입니다.', '김철수', CURRENT_TIMESTAMP, 15
    UNION ALL
    SELECT 'MyBatis 사용법 정리', 'MyBatis는 SQL 맵퍼 프레임워크로, SQL을 직접 작성하면서 객체 매핑을 자동으로 수행합니다.', '이영희', DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 23
    UNION ALL
    SELECT 'Thymeleaf 템플릿 엔진', 'Thymeleaf는 자연스러운 템플릿 문법을 제공하는 Java 템플릿 엔진입니다.', '박민준', DATEADD('HOUR', -4, CURRENT_TIMESTAMP), 8
    UNION ALL
    SELECT 'JPA vs MyBatis 비교', 'JPA는 ORM, MyBatis는 SQL 맵퍼입니다. 각각의 장단점을 알아봅시다.', '최윤서', DATEADD('DAY', -1, CURRENT_TIMESTAMP), 42
    UNION ALL
    SELECT 'H2 인메모리 데이터베이스', 'H2는 가볍고 빠른 인메모리 데이터베이스로 테스트에 많이 사용됩니다.', '정동욱', DATEADD('DAY', -1, CURRENT_TIMESTAMP), 31
    UNION ALL
    SELECT 'REST API 설계 원칙', 'REST 아키텍처를 따르는 API를 설계하는 방법과 모범 사례를 알아봅시다.', '김철수', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 19
    UNION ALL
    SELECT '트랜잭션 관리 전략', '@Transactional 어노테이션을 이용한 선언적 트랜잭션 관리 방법을 살펴봅시다.', '이영희', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 27
    UNION ALL
    SELECT 'Lombok을 활용한 코드 간결화', 'Lombok의 @Data, @Builder 등의 어노테이션을 활용하여 보일러플레이트 코드를 줄여봅시다.', '박민준', DATEADD('DAY', -3, CURRENT_TIMESTAMP), 36
    UNION ALL
    SELECT '페이지네이션 구현하기', '대용량 데이터를 효율적으로 처리하기 위한 페이지네이션 구현 방법입니다.', '최윤서', DATEADD('DAY', -3, CURRENT_TIMESTAMP), 44
    UNION ALL
    SELECT '테스트 주도 개발 (TDD)', 'Spring Boot와 JUnit을 이용한 효과적인 테스트 작성 방법을 배워봅시다.', '정동욱', DATEADD('DAY', -4, CURRENT_TIMESTAMP), 52
) AS tmp (TITLE, CONTENT, AUTHOR, CREATED_DATE, VIEW_COUNT)
WHERE NOT EXISTS (SELECT 1 FROM BOARD);