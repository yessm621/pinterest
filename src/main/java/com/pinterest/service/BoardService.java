package com.pinterest.service;

import com.pinterest.dto.BoardDto;
import com.pinterest.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;

    public Page<BoardDto> searchBoards(String searchKeyword, Pageable pageable) {
        return Page.empty();
    }

    public BoardDto searchBoard(long boardId) {
        return null;
    }

    public BoardDto getBoard(Long boardId) {
        return null;
    }

    @Transactional
    public void saveBoard(BoardDto boardDto) {
    }

    @Transactional
    public void updateBoard(BoardDto boardDto) {
    }

    @Transactional
    public void deleteBoard(long boardId) {
    }
}
