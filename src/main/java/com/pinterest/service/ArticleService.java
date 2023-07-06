package com.pinterest.service;

import com.pinterest.domain.Article;
import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import com.pinterest.domain.SearchType;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.ArticleWithCommentDto;
import com.pinterest.repository.ArticleRepository;
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
public class ArticleService {

    private final BoardRepository boardRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        if (searchType.equals(SearchType.TITLE)) {
            return articleRepository.findByTitleContaining(searchKeyword, pageable)
                    .map(ArticleDto::from);
        } else {
            return articleRepository.findByHashtag("#" + searchKeyword, pageable)
                    .map(ArticleDto::from);
        }
    }

    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId).map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));
    }

    public List<ArticleDto> getArticles(String email) {
        return articleRepository.findByMember_Email(email).stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    public List<ArticleDto> getArticles(Long memberId) {
        return articleRepository.findByMember_Id(memberId).stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    public ArticleWithCommentDto getArticleWithComment(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));
    }

    @Transactional
    public void saveArticle(ArticleDto dto) {
        Member member = memberRepository.findByEmail(dto.getMemberDto().getEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));
        Board board = boardRepository.getReferenceById(dto.getBoardId());
        articleRepository.save(dto.toEntity(member, board));
    }

    @Transactional
    public void updateArticle(Long articleId, ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(articleId);
            Member member = memberRepository.findByEmail(dto.getMemberDto().getEmail())
                    .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));

            if (article.getMember().getEmail().equals(member.getEmail())) {
                if (dto.getTitle() != null) {
                    article.setTitle(dto.getTitle());
                }
                if (dto.getContent() != null) {
                    article.setContent(dto.getContent());
                }
                if (dto.getImage() != null) {
                    article.setImage(dto.getImage());
                }
                if (dto.getHashtag() != null) {
                    article.setHashtag(dto.getHashtag());
                }
            }
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void deleteArticle(Long articleId, String email) {
        articleRepository.deleteByIdAndMember_Email(articleId, email);
    }
}
