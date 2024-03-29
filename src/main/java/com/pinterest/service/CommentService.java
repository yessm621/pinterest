package com.pinterest.service;

import com.pinterest.domain.Article;
import com.pinterest.domain.Member;
import com.pinterest.dto.CommentDto;
import com.pinterest.error.PinterestException;
import com.pinterest.repository.ArticleRepository;
import com.pinterest.repository.CommentRepository;
import com.pinterest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveComment(CommentDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.getArticleId());
            Member member = memberRepository.findByEmail(dto.getMemberDto().getEmail())
                    .orElseThrow(() -> new PinterestException("회원 정보가 없습니다."));
            commentRepository.save(dto.toEntity(article, member));
        } catch (PinterestException e) {
            log.warn("댓글 저장 실패. 댓글의 게시글을 찾을 수 없습니다.");
        }
    }
}
