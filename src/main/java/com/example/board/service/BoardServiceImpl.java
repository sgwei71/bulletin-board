package com.example.board.service;

import com.example.board.dao.BoardMapper;
import com.example.board.domain.Board;
import com.example.board.domain.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    /**
     * 게시물 목록 조회 (페이지네이션)
     * readOnly 트랜잭션으로 처리 (SELECT 쿼리만 사용)
     */
    @Override
    public PageInfo getListWithPage(Integer pageNum) {
        // 기본값 처리
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }

        // PageInfo 객체 생성
        PageInfo pageInfo = PageInfo.builder()
                .pageNum(pageNum)
                .pageSize(10)
                .totalCount(boardMapper.getTotalCount())
                .build();

        // 페이지네이션 정보 계산
        pageInfo.calculatePageInfo();

        // 현재 페이지에 해당하는 게시물 목록 조회
        List<Board> boardList = boardMapper.getList(pageInfo.getOffset(), pageInfo.getPageSize());
        pageInfo.setBoardList(boardList);

        log.debug("게시물 목록 조회: pageNum={}, totalCount={}, totalPage={}",
                pageNum, pageInfo.getTotalCount(), pageInfo.getTotalPage());

        return pageInfo;
    }

    /**
     * 게시물 상세 조회
     * readOnly 트랜잭션으로 처리
     */
    @Override
    public Board getById(Long boardId) {
        // 게시물 조회
        Board board = boardMapper.getById(boardId);

        if (board != null) {
            log.debug("게시물 상세 조회: boardId={}, title={}", boardId, board.getTitle());
        } else {
            log.warn("존재하지 않는 게시물: boardId={}", boardId);
        }

        return board;
    }

    /**
     * 게시물 생성
     * readOnly=false로 오버라이드 (INSERT 쿼리 실행)
     */
    @Override
    @Transactional(readOnly = false)
    public Long create(Board board) {
        boardMapper.insert(board);

        log.info("게시물 생성 완료: boardId={}, title={}, author={}",
                board.getBoardId(), board.getTitle(), board.getAuthor());

        return board.getBoardId();
    }

    /**
     * 게시물 수정
     * readOnly=false로 오버라이드 (UPDATE 쿼리 실행)
     */
    @Override
    @Transactional(readOnly = false)
    public void update(Board board) {
        boardMapper.update(board);

        log.info("게시물 수정 완료: boardId={}, title={}", board.getBoardId(), board.getTitle());
    }

    /**
     * 게시물 삭제
     * readOnly=false로 오버라이드 (DELETE 쿼리 실행)
     */
    @Override
    @Transactional(readOnly = false)
    public void delete(Long boardId) {
        boardMapper.delete(boardId);

        log.info("게시물 삭제 완료: boardId={}", boardId);
    }

}
