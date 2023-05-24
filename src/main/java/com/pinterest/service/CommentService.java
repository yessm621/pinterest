package com.pinterest.service;

import com.pinterest.domain.Article;
import com.pinterest.domain.Comment;
import com.pinterest.dto.CommentDto;
import com.pinterest.repository.ArticleRepository;
import com.pinterest.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentService {

    private final ArticleRepository articleRepository;

    private final CommentRepository commentRepository;

    public List<CommentDto> searchComment(Long articleId) {
        return commentRepository.findByArticle_Id(articleId)
                .stream()
                .map(CommentDto::from)
                .collect(Collectors.toList());
    }

    public void saveComment(CommentDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.getArticleId());
            commentRepository.save(dto.toEntity(article));
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패. 댓글의 게시글을 찾을 수 없습니다.");
        }
    }

    public void updateComment(CommentDto dto) {
        try {
            Comment comment = commentRepository.getReferenceById(dto.getId());
            if (dto.getContent() != null) {
                comment.setContent(dto.getContent());
            }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 업데이트 실패. 댓글을 찾을 수 없습니다.");
        }
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
