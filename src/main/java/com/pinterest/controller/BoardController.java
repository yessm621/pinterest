package com.pinterest.controller;

import com.pinterest.dto.BoardDto;
import com.pinterest.dto.BoardWithArticleDto;
import com.pinterest.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public String boards(@RequestParam(required = false) String searchKeyword,
                         @PageableDefault Pageable pageable,
                         Model model) {
        Page<BoardDto> boards = boardService.searchBoards(searchKeyword, pageable);
        model.addAttribute("boards", boards);
        return "boards/index";
    }

    @GetMapping("/{boardId}")
    public String boardDetail(@PathVariable Long boardId, Model model) {
        BoardWithArticleDto board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        model.addAttribute("articles", board.getArticleDtoList());
        return "boards/detail";
    }

    @GetMapping("/create")
    public String create() {
        return "boards/create";
    }
}
