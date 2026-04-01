package com.example.board.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Board 도메인 검증 테스트")
class BoardValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("유효한 Board 객체 - 검증 통과")
    void testValidBoard() {
        // Given
        Board board = Board.builder()
                .title("유효한 제목")
                .content("유효한 내용입니다.")
                .author("작성자")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("빈 제목 - 검증 실패")
    void testEmptyTitle() {
        // Given
        Board board = Board.builder()
                .title("")
                .content("유효한 내용입니다.")
                .author("작성자")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getMessage()).contains("필수")
        );
    }

    @Test
    @DisplayName("null 제목 - 검증 실패")
    void testNullTitle() {
        // Given
        Board board = Board.builder()
                .title(null)
                .content("유효한 내용입니다.")
                .author("작성자")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("제목 길이 부족 (1자) - 검증 실패")
    void testTitleTooShort() {
        // Given
        Board board = Board.builder()
                .title("a")
                .content("유효한 내용입니다.")
                .author("작성자")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getMessage()).contains("2자 이상")
        );
    }

    @Test
    @DisplayName("제목 길이 초과 (201자) - 검증 실패")
    void testTitleTooLong() {
        // Given
        String longTitle = "a".repeat(201);
        Board board = Board.builder()
                .title(longTitle)
                .content("유효한 내용입니다.")
                .author("작성자")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getMessage()).contains("200자 이하")
        );
    }

    @Test
    @DisplayName("최소 제목 길이 (2자) - 검증 통과")
    void testMinimumTitle() {
        // Given
        Board board = Board.builder()
                .title("ab")
                .content("유효한 내용입니다.")
                .author("작성자")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("최대 제목 길이 (200자) - 검증 통과")
    void testMaximumTitle() {
        // Given
        String maxTitle = "a".repeat(200);
        Board board = Board.builder()
                .title(maxTitle)
                .content("유효한 내용입니다.")
                .author("작성자")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("빈 내용 - 검증 실패")
    void testEmptyContent() {
        // Given
        Board board = Board.builder()
                .title("유효한 제목")
                .content("")
                .author("작성자")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("null 내용 - 검증 실패")
    void testNullContent() {
        // Given
        Board board = Board.builder()
                .title("유효한 제목")
                .content(null)
                .author("작성자")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("내용 길이 부족 (4자) - 검증 실패")
    void testContentTooShort() {
        // Given
        Board board = Board.builder()
                .title("유효한 제목")
                .content("1234")
                .author("작성자")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getMessage()).contains("5자 이상")
        );
    }

    @Test
    @DisplayName("최소 내용 길이 (5자) - 검증 통과")
    void testMinimumContent() {
        // Given
        Board board = Board.builder()
                .title("유효한 제목")
                .content("12345")
                .author("작성자")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("빈 작성자 - 검증 실패")
    void testEmptyAuthor() {
        // Given
        Board board = Board.builder()
                .title("유효한 제목")
                .content("유효한 내용입니다.")
                .author("")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("null 작성자 - 검증 실패")
    void testNullAuthor() {
        // Given
        Board board = Board.builder()
                .title("유효한 제목")
                .content("유효한 내용입니다.")
                .author(null)
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("작성자 길이 부족 (1자) - 검증 실패")
    void testAuthorTooShort() {
        // Given
        Board board = Board.builder()
                .title("유효한 제목")
                .content("유효한 내용입니다.")
                .author("a")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getMessage()).contains("2자 이상")
        );
    }

    @Test
    @DisplayName("작성자 길이 초과 (51자) - 검증 실패")
    void testAuthorTooLong() {
        // Given
        String longAuthor = "a".repeat(51);
        Board board = Board.builder()
                .title("유효한 제목")
                .content("유효한 내용입니다.")
                .author(longAuthor)
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anySatisfy(v ->
                assertThat(v.getMessage()).contains("50자 이하")
        );
    }

    @Test
    @DisplayName("최소 작성자 길이 (2자) - 검증 통과")
    void testMinimumAuthor() {
        // Given
        Board board = Board.builder()
                .title("유효한 제목")
                .content("유효한 내용입니다.")
                .author("ab")
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("최대 작성자 길이 (50자) - 검증 통과")
    void testMaximumAuthor() {
        // Given
        String maxAuthor = "a".repeat(50);
        Board board = Board.builder()
                .title("유효한 제목")
                .content("유효한 내용입니다.")
                .author(maxAuthor)
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("모든 필드가 유효한 경계값 - 검증 통과")
    void testBoundaryValues() {
        // Given
        Board board = Board.builder()
                .title("ab")  // 최소 길이
                .content("12345")  // 최소 길이
                .author("ab")  // 최소 길이
                .build();

        // When
        Set<ConstraintViolation<Board>> violations = validator.validate(board);

        // Then
        assertThat(violations).isEmpty();
    }
}
