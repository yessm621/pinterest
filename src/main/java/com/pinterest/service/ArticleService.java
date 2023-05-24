package com.pinterest.service;

import com.pinterest.domain.Article;
import com.pinterest.domain.Board;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.ArticleWithCommentDto;
import com.pinterest.dto.SearchType;
import com.pinterest.repository.ArticleRepository;
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
public class ArticleService {

    private final BoardRepository boardRepository;

    private final ArticleRepository articleRepository;

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

    public ArticleWithCommentDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));
    }

    @Transactional
    public void saveArticle(ArticleDto dto) {
        Board board = boardRepository.getReferenceById(dto.getBoardId());

        articleRepository.save(dto.toEntity(board));
    }

    @Transactional
    public void updateArticle(ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.getId());
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
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void deleteArticles(Long articleId) {
        articleRepository.deleteById(articleId);
    }
}
