package com.pinterest.dto;

import com.pinterest.domain.Article;
import com.pinterest.domain.Comment;
import com.pinterest.domain.Member;
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

    public static CommentDto of(Long articleId, MemberDto memberDto, String content) {
        return new CommentDto(null, articleId, memberDto, content, null);
    }

    public static CommentDto of(Long id, Long articleId, MemberDto memberDto, String content, LocalDateTime createdAt) {
        return new CommentDto(id, articleId, memberDto, content, createdAt);
    }

    // entity -> dto
    public static CommentDto from(Comment entity) {
        return new CommentDto(
                entity.getId(),
                entity.getArticle().getId(),
                MemberDto.from(entity.getMember()),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }

    // dto -> entity
    public Comment toEntity(Article article, Member member) {
        return Comment.of(
                member,
                article,
                content
        );
    }
}
