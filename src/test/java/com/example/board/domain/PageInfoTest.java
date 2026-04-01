package com.example.board.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PageInfo 페이지네이션 계산 테스트")
class PageInfoTest {

    @Test
    @DisplayName("첫 번째 페이지 - offset과 페이지 정보 계산")
    void testFirstPage() {
        // Given
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(1)
                .pageSize(10)
                .totalCount(25)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getOffset()).isEqualTo(0);
        assertThat(pageInfo.getTotalPage()).isEqualTo(3);
        assertThat(pageInfo.getStartPage()).isEqualTo(1);
        assertThat(pageInfo.getEndPage()).isEqualTo(3);
        assertThat(pageInfo.getHasPrev()).isFalse();
        assertThat(pageInfo.getHasNext()).isFalse();
    }

    @Test
    @DisplayName("두 번째 페이지 - offset과 페이지 정보 계산")
    void testSecondPage() {
        // Given
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(2)
                .pageSize(10)
                .totalCount(25)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getOffset()).isEqualTo(10);
        assertThat(pageInfo.getTotalPage()).isEqualTo(3);
        assertThat(pageInfo.getStartPage()).isEqualTo(1);
        assertThat(pageInfo.getEndPage()).isEqualTo(3);
        assertThat(pageInfo.getHasPrev()).isFalse();
        assertThat(pageInfo.getHasNext()).isFalse();
    }

    @Test
    @DisplayName("마지막 페이지 - offset과 페이지 정보 계산")
    void testLastPage() {
        // Given
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(3)
                .pageSize(10)
                .totalCount(25)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getOffset()).isEqualTo(20);
        assertThat(pageInfo.getTotalPage()).isEqualTo(3);
        assertThat(pageInfo.getStartPage()).isEqualTo(1);
        assertThat(pageInfo.getEndPage()).isEqualTo(3);
        assertThat(pageInfo.getHasPrev()).isFalse();
        assertThat(pageInfo.getHasNext()).isFalse();
    }

    @Test
    @DisplayName("페이지 네비게이션 - 6~10번째 페이지")
    void testPageNavigationSecondBlock() {
        // Given
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(6)
                .pageSize(10)
                .totalCount(150)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getOffset()).isEqualTo(50);
        assertThat(pageInfo.getTotalPage()).isEqualTo(15);
        assertThat(pageInfo.getStartPage()).isEqualTo(6);
        assertThat(pageInfo.getEndPage()).isEqualTo(10);
        assertThat(pageInfo.getHasPrev()).isTrue();
        assertThat(pageInfo.getHasNext()).isTrue();
    }

    @Test
    @DisplayName("데이터가 정확히 페이지 크기의 배수 - totalPage 계산")
    void testTotalPageWithExactMultiple() {
        // Given
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(1)
                .pageSize(10)
                .totalCount(50)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getTotalPage()).isEqualTo(5);
    }

    @Test
    @DisplayName("데이터가 페이지 크기보다 작은 경우 - 1페이지만 필요")
    void testSinglePageData() {
        // Given
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(1)
                .pageSize(10)
                .totalCount(5)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getTotalPage()).isEqualTo(1);
        assertThat(pageInfo.getEndPage()).isEqualTo(1);
        assertThat(pageInfo.getHasPrev()).isFalse();
        assertThat(pageInfo.getHasNext()).isFalse();
    }

    @Test
    @DisplayName("데이터가 없는 경우 - totalCount 0")
    void testEmptyData() {
        // Given
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(1)
                .pageSize(10)
                .totalCount(0)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getTotalPage()).isEqualTo(0);
        assertThat(pageInfo.getEndPage()).isEqualTo(0);
        assertThat(pageInfo.getHasPrev()).isFalse();
        assertThat(pageInfo.getHasNext()).isFalse();
    }

    @Test
    @DisplayName("offset 계산 - 페이지 번호가 높은 경우")
    void testLargePageNumber() {
        // Given
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(100)
                .pageSize(10)
                .totalCount(1000)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getOffset()).isEqualTo(990);
        assertThat(pageInfo.getTotalPage()).isEqualTo(100);
    }

    @Test
    @DisplayName("페이지 네비게이션 블록 변경 시 startPage 계산")
    void testPageNavigationBlockChange() {
        // Given
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(5)
                .pageSize(10)
                .totalCount(100)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getStartPage()).isEqualTo(1);
        assertThat(pageInfo.getEndPage()).isEqualTo(5);

        // Given
        pageInfo.setPageNum(6);

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getStartPage()).isEqualTo(6);
        assertThat(pageInfo.getEndPage()).isEqualTo(10);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("첫 번째 네비게이션 블록 페이지들 - startPage는 1")
    void testFirstBlockPageNumbers(int pageNum) {
        // Given
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pageNum)
                .pageSize(10)
                .totalCount(100)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getStartPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("endPage가 totalPage를 초과하지 않음")
    void testEndPageNotExceedTotal() {
        // Given
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(1)
                .pageSize(10)
                .totalCount(32)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getTotalPage()).isEqualTo(4);
        assertThat(pageInfo.getEndPage()).isLessThanOrEqualTo(pageInfo.getTotalPage());
        assertThat(pageInfo.getEndPage()).isEqualTo(4);
    }

    @Test
    @DisplayName("다양한 pageSize로 계산")
    void testDifferentPageSize() {
        // Given - pageSize가 20인 경우
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(2)
                .pageSize(20)
                .totalCount(100)
                .build();

        // When
        pageInfo.calculatePageInfo();

        // Then
        assertThat(pageInfo.getOffset()).isEqualTo(20);
        assertThat(pageInfo.getTotalPage()).isEqualTo(5);
    }
}
