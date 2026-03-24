package com.example.board.controller;

import com.example.board.domain.Board;
import com.example.board.domain.PageInfo;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시물 목록 조회
     * GET /board/list
     */
    @GetMapping("/list")
    public String list(@RequestParam(required = false, defaultValue = "1") Integer page,
                      Model model) {
        PageInfo pageInfo = boardService.getListWithPage(page);

        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("boardList", pageInfo.getBoardList());

        log.debug("게시물 목록 페이지 접근: page={}", page);

        return "board/list";
    }

    /**
     * 게시물 작성 폼 (GET)
     * GET /board/write
     */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("board", new Board());

        log.debug("게시물 작성 폼 요청");

        return "board/write";
    }

    /**
     * 게시물 작성 처리 (POST)
     * POST /board/write
     * PRG 패턴: 작성 후 상세 페이지로 리다이렉트
     */
    @PostMapping("/write")
    public String writeProcess(@Valid @ModelAttribute("board") Board board,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        // 입력 검증 실패 시 폼으로 다시 이동
        if (bindingResult.hasErrors()) {
            log.warn("게시물 작성 입력 검증 실패");
            return "board/write";
        }

        Long boardId = boardService.create(board);

        redirectAttributes.addFlashAttribute("message", "게시물이 작성되었습니다.");

        log.info("게시물 작성 완료: boardId={}, title={}", boardId, board.getTitle());

        return "redirect:/board/" + boardId;
    }

    /**
     * 게시물 상세 조회
     * GET /board/{id}
     */
    @GetMapping("/{id}")
    public String view(@PathVariable Long id,
                      Model model) {
        Board board = boardService.getById(id);

        if (board == null) {
            log.warn("존재하지 않는 게시물 요청: id={}", id);
            return "redirect:/board/list";
        }

        model.addAttribute("board", board);

        log.debug("게시물 상세 조회: id={}, title={}", id, board.getTitle());

        return "board/view";
    }

    /**
     * 게시물 수정 폼 (GET)
     * GET /board/{id}/edit
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                          Model model) {
        Board board = boardService.getById(id);

        if (board == null) {
            log.warn("존재하지 않는 게시물 수정 요청: id={}", id);
            return "redirect:/board/list";
        }

        model.addAttribute("board", board);
        model.addAttribute("isEdit", true);  // 템플릿에서 수정 모드 판단용

        log.debug("게시물 수정 폼 요청: id={}", id);

        return "board/write";
    }

    /**
     * 게시물 수정 처리 (POST)
     * POST /board/{id}/edit
     * PRG 패턴: 수정 후 상세 페이지로 리다이렉트
     */
    @PostMapping("/{id}/edit")
    public String editProcess(@PathVariable Long id,
                             @Valid @ModelAttribute("board") Board board,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        // 입력 검증 실패 시 폼으로 다시 이동
        if (bindingResult.hasErrors()) {
            log.warn("게시물 수정 입력 검증 실패: id={}", id);
            return "board/write";
        }

        board.setBoardId(id);
        boardService.update(board);

        redirectAttributes.addFlashAttribute("message", "게시물이 수정되었습니다.");

        log.info("게시물 수정 완료: id={}, title={}", id, board.getTitle());

        return "redirect:/board/" + id;
    }

    /**
     * 게시물 삭제 처리 (POST)
     * POST /board/{id}/delete
     * PRG 패턴: 삭제 후 목록 페이지로 리다이렉트
     */
    @PostMapping("/{id}/delete")
    public String deleteProcess(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        Board board = boardService.getById(id);

        if (board == null) {
            log.warn("존재하지 않는 게시물 삭제 요청: id={}", id);
            return "redirect:/board/list";
        }

        boardService.delete(id);

        redirectAttributes.addFlashAttribute("message", "게시물이 삭제되었습니다.");

        log.info("게시물 삭제 완료: id={}", id);

        return "redirect:/board/list";
    }

}
