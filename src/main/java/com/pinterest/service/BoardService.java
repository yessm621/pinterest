package com.pinterest.service;

import com.pinterest.domain.Board;
import com.pinterest.dto.BoardDto;
import com.pinterest.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;

    public Page<BoardDto> searchBoards(String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return boardRepository.findAll(pageable)
                    .map(BoardDto::from);
        }

        return boardRepository.findByTitleContaining(searchKeyword, pageable)
                .map(BoardDto::from);
    }

    public BoardDto getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .map(BoardDto::from)
                .orElseThrow(() -> new EntityNotFoundException("보드가 없습니다."));
    }

    @Transactional
    public void saveBoard(BoardDto dto) {
        boardRepository.save(dto.toEntity());
    }

    @Transactional
    public void updateBoard(BoardDto dto) {
        try {
            Board board = boardRepository.getReferenceById(dto.getId());
            if (dto.getTitle() != null) {
                board.setTitle(dto.getTitle());
            }
            if (dto.getImage() != null) {
                board.setImage(dto.getImage());
            }
        } catch (EntityNotFoundException e) {
            log.warn("보드 업데이트 실패. 보드를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void deleteBoard(long boardId) {
        boardRepository.deleteById(boardId);
    }
}
