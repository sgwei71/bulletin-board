package com.example.board.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageInfo {

    // 페이지 관련 정보
    private Integer pageNum = 1;           // 현재 페이지 번호
    private Integer pageSize = 10;         // 한 페이지의 행 수
    private Integer totalCount = 0;        // 전체 데이터 행 수

    // 계산된 값
    private Integer offset;                // SELECT OFFSET 값
    private Integer totalPage;             // 전체 페이지 수
    private Integer startPage;             // 페이지 네비게이션 시작 번호
    private Integer endPage;               // 페이지 네비게이션 끝 번호
    private Boolean hasPrev;               // 이전 페이지 존재 여부
    private Boolean hasNext;               // 다음 페이지 존재 여부

    // 데이터
    private List<Board> boardList;         // 현재 페이지의 게시물 목록

    private static final Integer PAGE_NAVI_COUNT = 5;  // 페이지 네비게이션에 표시할 페이지 개수

    public void calculatePageInfo() {
        // offset 계산 (MyBatis LIMIT에서 사용)
        this.offset = (pageNum - 1) * pageSize;

        // 전체 페이지 수 계산
        this.totalPage = (totalCount + pageSize - 1) / pageSize;

        // 페이지 네비게이션 시작 페이지 계산
        Integer temp = (pageNum - 1) / PAGE_NAVI_COUNT;
        this.startPage = temp * PAGE_NAVI_COUNT + 1;

        // 페이지 네비게이션 끝 페이지 계산
        this.endPage = Math.min(startPage + PAGE_NAVI_COUNT - 1, totalPage);

        // 이전/다음 버튼 존재 여부 계산
        this.hasPrev = startPage > 1;
        this.hasNext = endPage < totalPage;
    }

}
