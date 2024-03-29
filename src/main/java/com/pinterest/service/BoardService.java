package com.pinterest.service;

import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import com.pinterest.dto.BoardDto;
import com.pinterest.error.PinterestException;
import com.pinterest.repository.BoardRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.query.BoardQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;
    private final MemberRepository memberRepository;

    public Page<BoardDto> searchBoards(String email, Pageable pageable) {
        return boardQueryRepository.searchBoards(email, pageable)
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
                .orElseThrow(() -> new PinterestException("보드가 없습니다."));
    }

    @Transactional
    public void saveBoard(BoardDto dto) {
        Member member = memberRepository.findByEmail(dto.getMemberDto().getEmail())
                .orElseThrow(() -> new PinterestException("회원 정보가 없습니다."));
        boardTitleDuplicated(dto);
        if (dto.getTitle() != null && !dto.getTitle().equals("")) {
            boardRepository.save(dto.toEntity(member));
        }
    }

    @Transactional
    public void updateBoard(Long boardId, BoardDto dto) {
        try {
            Board board = boardRepository.getReferenceById(boardId);
            Member member = memberRepository.findByEmail(dto.getMemberDto().getEmail())
                    .orElseThrow(() -> new PinterestException("회원 정보가 없습니다."));
            boardTitleDuplicated(dto);
            if (board.getMember().getEmail().equals(member.getEmail())) {
                if (dto.getTitle() != null) {
                    board.setTitle(dto.getTitle());
                }
            }
        } catch (PinterestException e) {
            log.warn("보드 업데이트 실패. 보드를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void deleteBoard(long boardId, String email) {
        boardRepository.deleteByIdAndMember_Email(boardId, email);
    }

    private void boardTitleDuplicated(BoardDto dto) {
        Optional<Board> board = boardRepository.findByMemberIdAndTitle(dto.getMemberDto().getId(), dto.getTitle());
        if (board.isPresent()) {
            throw new PinterestException("이미 존재하는 보드 이름 입니다.");
        }
    }
}
