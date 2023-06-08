package com.pinterest.service;

import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.BoardWithArticleDto;
import com.pinterest.repository.BoardRepository;
import com.pinterest.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    public Page<BoardDto> searchBoards(String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return boardRepository.findAll(pageable)
                    .map(BoardDto::from);
        }

        return boardRepository.findByTitleContaining(searchKeyword, pageable)
                .map(BoardDto::from);
    }

    public BoardWithArticleDto getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .map(BoardWithArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("보드가 없습니다."));
    }

    @Transactional
    public void saveBoard(BoardDto dto) {
        Member member = memberRepository.getReferenceById(dto.getMemberDto().getId());
        boardRepository.save(dto.toEntity(member));
    }

    @Transactional
    public void updateBoard(Long boardId, BoardDto dto) {
        try {
            Board board = boardRepository.getReferenceById(boardId);
            Member member = memberRepository.getReferenceById(dto.getMemberDto().getId());

            if (board.getMember().equals(member)) {
                if (dto.getTitle() != null) {
                    board.setTitle(dto.getTitle());
                }
                if (dto.getImage() != null) {
                    board.setImage(dto.getImage());
                }
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
