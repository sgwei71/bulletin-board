package com.example.board.service;

import com.example.board.domain.Board;
import com.example.board.domain.PageInfo;

import java.util.List;

public interface BoardService {

    /**
     * 게시물 목록 조회 (페이지네이션)
     */
    PageInfo getListWithPage(Integer pageNum);

    /**
     * 게시물 상세 조회
     */
    Board getById(Long boardId);

    /**
     * 게시물 생성
     */
    Long create(Board board);

    /**
     * 게시물 수정
     */
    void update(Board board);

    /**
     * 게시물 삭제
     */
    void delete(Long boardId);

}
