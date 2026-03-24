package com.example.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * 메인 페이지 - 게시물 목록으로 리다이렉트
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/board/list";
    }

}
