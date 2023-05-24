package com.pinterest.dto;

import com.pinterest.domain.Article;
import com.pinterest.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;
    private Long articleId;
    private MemberDto memberDto;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static CommentDto of(Long id, Long articleId, MemberDto memberDto, String content, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new CommentDto(id, articleId, memberDto, content, createdAt, modifiedAt);
    }

    // entity -> dto
    public static CommentDto from(Comment entity) {
        return new CommentDto(
                entity.getId(),
                entity.getArticle().getId(),
                MemberDto.from(entity.getMember()),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }

    // dto -> entity
    public Comment toEntity(Article entity) {
        return Comment.of(
                memberDto.toEntity(),
                entity,
                content
        );
    }
}
