package com.pinterest.controller;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.BoardWithArticleDto;
import com.pinterest.dto.request.BoardArticleRequest;
import com.pinterest.dto.request.BoardRequest;
import com.pinterest.dto.response.BoardResponse;
import com.pinterest.service.BoardService;
import com.pinterest.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final PaginationService paginationService;

    @GetMapping
    public String boards(@PageableDefault(size = 30) Pageable pageable,
                         @AuthenticationPrincipal CustomUserDetails customUserDetails,
                         Model model) {
        Page<BoardDto> boards = boardService.searchBoards(customUserDetails.getUsername(), pageable);
        List<Integer> pagination = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), boards.getTotalPages());

        model.addAttribute("boards", boards);
        model.addAttribute("pagination", pagination);

        return "boards/index";
    }

    @GetMapping("/{boardId}")
    public String boardDetail(@PathVariable Long boardId, @AuthenticationPrincipal CustomUserDetails customUserDetails,
                              Model model) {
        BoardWithArticleDto board = boardService.getBoardWithArticles(boardId);
        model.addAttribute("board", board);
        model.addAttribute("articles", board.getArticleDtoList());

        return "boards/detail";
    }

    @GetMapping("/create")
    public String boardCreate() {
        return "boards/create";
    }

    @PostMapping("/create")
    public String boardCreate(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                              BoardRequest boardRequest) {
        boardService.saveBoard(boardRequest.toDto(customUserDetails.toDto()));
        return "redirect:/boards";
    }

    /**
     * 핀 생성 페이지에서 보드 생성
     */
    @PostMapping("/form")
    public String boardForm(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                            BoardArticleRequest boardArticleRequest) {
        boardService.saveBoard(boardArticleRequest.toDto(customUserDetails.toDto()));
        return "redirect:/articles/form";
    }

    @GetMapping("/{boardId}/form")
    public String boardUpdateForm(@PathVariable Long boardId,
                                  Model model) {
        BoardResponse board = BoardResponse.from(boardService.getBoard(boardId));
        model.addAttribute("board", board);
        return "boards/updateForm";
    }

    @PostMapping("/{boardId}/form")
    public String boardUpdateForm(@PathVariable Long boardId,
                                  @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                  BoardRequest boardRequest) {
        boardService.updateBoard(boardId, boardRequest.toDto(customUserDetails.toDto()));
        return "redirect:/boards";
    }

    @PostMapping("/{boardId}/delete")
    public String boardDelete(@PathVariable Long boardId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boardService.deleteBoard(boardId, customUserDetails.getUsername());
        return "redirect:/boards";
    }
}
