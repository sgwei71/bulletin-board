package com.example.board.service;

import com.example.board.dao.BoardMapper;
import com.example.board.domain.Board;
import com.example.board.domain.PageInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BoardService 단위 테스트")
class BoardServiceImplTest {

    @Mock
    private BoardMapper boardMapper;

    @InjectMocks
    private BoardServiceImpl boardService;

    private Board testBoard;
    private List<Board> testBoardList;

    @BeforeEach
    void setUp() {
        testBoard = Board.builder()
                .boardId(1L)
                .title("테스트 제목")
                .content("테스트 내용입니다.")
                .author("테스트 작성자")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .viewCount(10)
                .build();

        testBoardList = Arrays.asList(
                Board.builder()
                        .boardId(1L)
                        .title("첫 번째 글")
                        .content("첫 번째 내용")
                        .author("작성자1")
                        .viewCount(5)
                        .build(),
                Board.builder()
                        .boardId(2L)
                        .title("두 번째 글")
                        .content("두 번째 내용")
                        .author("작성자2")
                        .viewCount(3)
                        .build()
        );
    }

    @Test
    @DisplayName("게시물 목록 조회 - 페이지네이션 정보와 함께 반환")
    void testGetListWithPage() {
        // Given
        int pageNum = 1;
        when(boardMapper.getTotalCount()).thenReturn(25);
        when(boardMapper.getList(0, 10)).thenReturn(testBoardList);

        // When
        PageInfo pageInfo = boardService.getListWithPage(pageNum);

        // Then
        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.getPageNum()).isEqualTo(1);
        assertThat(pageInfo.getTotalCount()).isEqualTo(25);
        assertThat(pageInfo.getTotalPage()).isEqualTo(3);
        assertThat(pageInfo.getBoardList()).hasSize(2);

        verify(boardMapper, times(1)).getTotalCount();
        verify(boardMapper, times(1)).getList(0, 10);
    }

    @Test
    @DisplayName("게시물 목록 조회 - 페이지 번호가 null인 경우 기본값 처리")
    void testGetListWithPageNullPageNum() {
        // Given
        when(boardMapper.getTotalCount()).thenReturn(10);
        when(boardMapper.getList(0, 10)).thenReturn(testBoardList);

        // When
        PageInfo pageInfo = boardService.getListWithPage(null);

        // Then
        assertThat(pageInfo.getPageNum()).isEqualTo(1);
        verify(boardMapper).getList(0, 10);
    }

    @Test
    @DisplayName("게시물 목록 조회 - 페이지 번호가 음수인 경우 기본값 처리")
    void testGetListWithPageNegativePageNum() {
        // Given
        when(boardMapper.getTotalCount()).thenReturn(10);
        when(boardMapper.getList(0, 10)).thenReturn(testBoardList);

        // When
        PageInfo pageInfo = boardService.getListWithPage(-5);

        // Then
        assertThat(pageInfo.getPageNum()).isEqualTo(1);
        verify(boardMapper).getList(0, 10);
    }

    @Test
    @DisplayName("게시물 상세 조회 - 존재하는 게시물 반환")
    void testGetById() {
        // Given
        Long boardId = 1L;
        when(boardMapper.getById(boardId)).thenReturn(testBoard);

        // When
        Board board = boardService.getById(boardId);

        // Then
        assertThat(board).isNotNull();
        assertThat(board.getBoardId()).isEqualTo(1L);
        assertThat(board.getTitle()).isEqualTo("테스트 제목");
        assertThat(board.getAuthor()).isEqualTo("테스트 작성자");

        verify(boardMapper, times(1)).getById(boardId);
    }

    @Test
    @DisplayName("게시물 상세 조회 - 존재하지 않는 게시물 null 반환")
    void testGetByIdNotFound() {
        // Given
        Long boardId = 999L;
        when(boardMapper.getById(boardId)).thenReturn(null);

        // When
        Board board = boardService.getById(boardId);

        // Then
        assertThat(board).isNull();
        verify(boardMapper, times(1)).getById(boardId);
    }

    @Test
    @DisplayName("게시물 생성 - 새로운 게시물 저장 및 ID 반환")
    void testCreate() {
        // Given
        Board newBoard = Board.builder()
                .title("새 게시물")
                .content("새로운 내용입니다.")
                .author("새 작성자")
                .build();
        newBoard.setBoardId(1L);

        doNothing().when(boardMapper).insert(any(Board.class));

        // When
        Long boardId = boardService.create(newBoard);

        // Then
        assertThat(boardId).isEqualTo(1L);
        verify(boardMapper, times(1)).insert(newBoard);
    }

    @Test
    @DisplayName("게시물 수정 - 기존 게시물 업데이트")
    void testUpdate() {
        // Given
        Board updateBoard = Board.builder()
                .boardId(1L)
                .title("수정된 제목")
                .content("수정된 내용")
                .author("수정 작성자")
                .build();

        doNothing().when(boardMapper).update(any(Board.class));

        // When
        boardService.update(updateBoard);

        // Then
        verify(boardMapper, times(1)).update(updateBoard);
    }

    @Test
    @DisplayName("게시물 삭제 - 게시물 ID로 삭제")
    void testDelete() {
        // Given
        Long boardId = 1L;
        doNothing().when(boardMapper).delete(boardId);

        // When
        boardService.delete(boardId);

        // Then
        verify(boardMapper, times(1)).delete(boardId);
    }
}
