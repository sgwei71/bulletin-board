package com.example.board.dao;

import com.example.board.domain.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {

    /**
     * 전체 게시물 개수 조회
     */
    int getTotalCount();

    /**
     * 게시물 목록 조회 (페이지네이션)
     */
    List<Board> getList(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 게시물 상세 조회
     */
    Board getById(@Param("boardId") Long boardId);

    /**
     * 게시물 생성
     */
    void insert(Board board);

    /**
     * 게시물 수정
     */
    void update(Board board);

    /**
     * 게시물 삭제
     */
    void delete(@Param("boardId") Long boardId);

    /**
     * 조회수 증가
     */
    void increaseViewCount(@Param("boardId") Long boardId);

}
