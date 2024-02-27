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
import java.util.List;
import java.util.stream.Collectors;

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

    public List<BoardDto> getBoards(String email) {
        return boardRepository.findByMember_Email(email).stream()
                .map(BoardDto::from)
                .collect(Collectors.toList());
    }

    public BoardDto getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .map(BoardDto::from)
                .orElseThrow(() -> new EntityNotFoundException("보드가 없습니다."));
    }

    public BoardWithArticleDto getBoardWithArticles(Long boardId) {
        return boardRepository.findById(boardId)
                .map(BoardWithArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("보드가 없습니다."));
    }

    @Transactional
    public void saveBoard(BoardDto dto) {
        Member member = memberRepository.findByEmail(dto.getMemberDto().getEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));
        boardRepository.save(dto.toEntity(member));
    }

    @Transactional
    public void updateBoard(Long boardId, BoardDto dto) {
        try {
            Board board = boardRepository.getReferenceById(boardId);
            Member member = memberRepository.findByEmail(dto.getMemberDto().getEmail())
                    .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));

            if (board.getMember().getEmail().equals(member.getEmail())) {
                if (dto.getTitle() != null) {
                    board.setTitle(dto.getTitle());
                }
            }
        } catch (EntityNotFoundException e) {
            log.warn("보드 업데이트 실패. 보드를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void deleteBoard(long boardId, String email) {
        boardRepository.deleteByIdAndMember_Email(boardId, email);
    }
}
