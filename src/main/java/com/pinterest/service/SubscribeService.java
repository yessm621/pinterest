package com.pinterest.service;

import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import com.pinterest.domain.Subscribe;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.SubscribeDto;
import com.pinterest.repository.ArticleRepository;
import com.pinterest.repository.BoardRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.SubscribeRepository;
import com.pinterest.repository.query.SubscribeQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;
    private final SubscribeQueryRepository subscribeQueryRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ArticleRepository articleRepository;

    public List<ArticleDto> searchArticles(String email) {
        return articleRepository.findByMember_Email(email).stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    public List<BoardDto> searchBoards(String email) {
        return subscribeQueryRepository.findSubscribeBoards(email).stream()
                .map(BoardDto::from)
                .collect(Collectors.toList());
    }

    public boolean subscribeCheck(Long boardId, String email) {
        return subscribeRepository.findSubscribeByBoard_IdAndMember_Email(boardId, email)
                .isPresent();
    }

    @Transactional
    public void saveSubscribe(SubscribeDto dto) {
        Member member = memberRepository.findByEmail(dto.getMemberDto().getEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원이 없습니다."));
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("핀이 없습니다."));
        Optional<Subscribe> findBySubscribe = subscribeRepository
                .findSubscribeByBoard_IdAndMember_id(board.getId(), member.getId());
        if (findBySubscribe.isEmpty()) {
            subscribeRepository.save(dto.toEntity(member, board));
        } else {
            subscribeRepository.deleteById(findBySubscribe.get().getId());
        }
    }

    @Transactional
    public void cancelSubscribe(Long subscribe_id) {
        subscribeRepository.deleteById(subscribe_id);
    }
}
