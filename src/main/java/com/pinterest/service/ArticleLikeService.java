package com.pinterest.service;

import com.pinterest.domain.Article;
import com.pinterest.domain.ArticleLike;
import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.ArticleLikeDto;
import com.pinterest.error.PinterestException;
import com.pinterest.repository.ArticleLikeRepository;
import com.pinterest.repository.ArticleRepository;
import com.pinterest.repository.BoardRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.query.ArticleLikeQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleLikeService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleLikeQueryRepository articleLikeQueryRepository;

    /**
     * 보드 페이지에서 사용자가 저장한 핀 리스트를 조회
     */
    public List<ArticleDto> getArticleLikes(Long boardId, String email) {
        return articleLikeQueryRepository.getArticleLikes(boardId, email).stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 마이페이지에서 사용자가 저장한 핀 리스트를 조회
     */
    public List<ArticleDto> getArticleLikes(String email) {
        return articleLikeQueryRepository.getArticleLikes(null, email).stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 핀 저장 정보 조회
     */
    public ArticleLikeDto getArticleLike(Long articleId, String email) {
        ArticleLike articleLike = articleLikeQueryRepository.getArticleLike(articleId, email);
        return articleLike != null ? ArticleLikeDto.from(articleLike) : null;
    }

    /**
     * 핀 저장
     */
    @Transactional
    public void saveArticleLike(ArticleLikeDto dto) {
        Member member = memberRepository.findByEmail(dto.getMemberDto().getEmail())
                .orElseThrow(() -> new PinterestException("회원이 없습니다."));
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new PinterestException("보드가 없습니다."));
        Article article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new PinterestException("핀이 없습니다."));

        articleLikeRepository.save(dto.toEntity(member, board, article));
    }

    /**
     * 핀 저장 취소
     */
    @Transactional
    public void deleteArticleLike(Long articleLikeId) {
        ArticleLike articleLike = articleLikeRepository.findById(articleLikeId)
                .orElseThrow(() -> new PinterestException("핀을 찾을 수 없습니다."));
        articleLikeRepository.delete(articleLike);
    }
}
