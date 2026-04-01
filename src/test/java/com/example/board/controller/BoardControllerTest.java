package com.example.board.controller;

import com.example.board.domain.Board;
import com.example.board.domain.PageInfo;
import com.example.board.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@DisplayName("BoardController 통합 테스트")
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    private Board testBoard;
    private PageInfo testPageInfo;

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

        testPageInfo = PageInfo.builder()
                .pageNum(1)
                .pageSize(10)
                .totalCount(25)
                .totalPage(3)
                .boardList(Arrays.asList(testBoard))
                .build();
    }

    @Test
    @DisplayName("GET /board/list - 게시물 목록 페이지 조회")
    void testList() throws Exception {
        // Given
        when(boardService.getListWithPage(1)).thenReturn(testPageInfo);

        // When & Then
        mockMvc.perform(get("/board/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/list"))
                .andExpect(model().attributeExists("pageInfo", "boardList"));

        verify(boardService, times(1)).getListWithPage(1);
    }

    @Test
    @DisplayName("GET /board/list?page=2 - 페이지 번호를 지정한 목록 조회")
    void testListWithPageNumber() throws Exception {
        // Given
        testPageInfo.setPageNum(2);
        when(boardService.getListWithPage(2)).thenReturn(testPageInfo);

        // When & Then
        mockMvc.perform(get("/board/list").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/list"));

        verify(boardService, times(1)).getListWithPage(2);
    }

    @Test
    @DisplayName("GET /board/write - 게시물 작성 폼 조회")
    void testWriteForm() throws Exception {
        // When & Then
        mockMvc.perform(get("/board/write"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/write"))
                .andExpect(model().attributeExists("board"));
    }

    @Test
    @DisplayName("POST /board/write - 유효한 게시물 작성")
    void testWriteProcess() throws Exception {
        // Given
        testBoard.setBoardId(1L);
        when(boardService.create(any(Board.class))).thenReturn(1L);

        // When & Then
        mockMvc.perform(post("/board/write")
                .param("title", "새 제목")
                .param("content", "새로운 내용입니다.")
                .param("author", "작성자"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/1"));

        verify(boardService, times(1)).create(any(Board.class));
    }

    @Test
    @DisplayName("POST /board/write - 제목 검증 실패 (빈 값)")
    void testWriteProcessInvalidTitle() throws Exception {
        // When & Then
        mockMvc.perform(post("/board/write")
                .param("title", "")
                .param("content", "유효한 내용입니다.")
                .param("author", "작성자"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/write"));

        verify(boardService, never()).create(any(Board.class));
    }

    @Test
    @DisplayName("POST /board/write - 내용 검증 실패 (길이 부족)")
    void testWriteProcessInvalidContent() throws Exception {
        // When & Then
        mockMvc.perform(post("/board/write")
                .param("title", "유효한 제목")
                .param("content", "짧음")
                .param("author", "작성자"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/write"));

        verify(boardService, never()).create(any(Board.class));
    }

    @Test
    @DisplayName("GET /board/1 - 게시물 상세 조회")
    void testView() throws Exception {
        // Given
        when(boardService.getById(1L)).thenReturn(testBoard);

        // When & Then
        mockMvc.perform(get("/board/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/view"))
                .andExpect(model().attributeExists("board"))
                .andExpect(model().attribute("board", testBoard));

        verify(boardService, times(1)).getById(1L);
    }

    @Test
    @DisplayName("GET /board/999 - 존재하지 않는 게시물 조회 시 목록으로 리다이렉트")
    void testViewNotFound() throws Exception {
        // Given
        when(boardService.getById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/board/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));

        verify(boardService, times(1)).getById(999L);
    }

    @Test
    @DisplayName("GET /board/1/edit - 게시물 수정 폼 조회")
    void testEditForm() throws Exception {
        // Given
        when(boardService.getById(1L)).thenReturn(testBoard);

        // When & Then
        mockMvc.perform(get("/board/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/write"))
                .andExpect(model().attributeExists("board", "isEdit"))
                .andExpect(model().attribute("isEdit", true));

        verify(boardService, times(1)).getById(1L);
    }

    @Test
    @DisplayName("GET /board/999/edit - 존재하지 않는 게시물 수정 시 목록으로 리다이렉트")
    void testEditFormNotFound() throws Exception {
        // Given
        when(boardService.getById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/board/999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));

        verify(boardService, times(1)).getById(999L);
    }

    @Test
    @DisplayName("POST /board/1/edit - 유효한 게시물 수정")
    void testEditProcess() throws Exception {
        // Given
        doNothing().when(boardService).update(any(Board.class));

        // When & Then
        mockMvc.perform(post("/board/1/edit")
                .param("title", "수정된 제목")
                .param("content", "수정된 내용입니다.")
                .param("author", "수정 작성자"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/1"));

        verify(boardService, times(1)).update(any(Board.class));
    }

    @Test
    @DisplayName("POST /board/1/edit - 수정 검증 실패")
    void testEditProcessInvalid() throws Exception {
        // When & Then
        mockMvc.perform(post("/board/1/edit")
                .param("title", "")
                .param("content", "짧은 내용")
                .param("author", "작성자"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/write"));

        verify(boardService, never()).update(any(Board.class));
    }

    @Test
    @DisplayName("POST /board/1/delete - 게시물 삭제")
    void testDeleteProcess() throws Exception {
        // Given
        when(boardService.getById(1L)).thenReturn(testBoard);
        doNothing().when(boardService).delete(1L);

        // When & Then
        mockMvc.perform(post("/board/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));

        verify(boardService, times(1)).getById(1L);
        verify(boardService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("POST /board/999/delete - 존재하지 않는 게시물 삭제 시 목록으로 리다이렉트")
    void testDeleteProcessNotFound() throws Exception {
        // Given
        when(boardService.getById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/board/999/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));

        verify(boardService, times(1)).getById(999L);
        verify(boardService, never()).delete(anyLong());
    }
}
