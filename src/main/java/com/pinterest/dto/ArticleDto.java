package com.pinterest.dto;

import com.pinterest.domain.Article;
import com.pinterest.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {

    private Long id;
    private Long boardId;
    private MemberDto memberDto;
    private String title;
    private String content;
    private String image;
    private String hashtag;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ArticleDto of(Long id, Long boardId, MemberDto memberDto, String title, String content, String image, String hashtag, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new ArticleDto(id, boardId, memberDto, title, content, image, hashtag, createdAt, modifiedAt);
    }

    // entity -> dto
    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                entity.getBoard().getId(),
                MemberDto.from(entity.getMember()),
                entity.getTitle(),
                entity.getContent(),
                entity.getImage(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }

    // dto -> entity
    public Article toEntity(Board entity) {
        return Article.of(
                memberDto.toEntity(),
                entity,
                title,
                content,
                image,
                hashtag
        );
    }
}
