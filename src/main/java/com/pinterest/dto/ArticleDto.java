package com.pinterest.dto;

import com.pinterest.domain.Article;
import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
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

    public static ArticleDto of(Long boardId, MemberDto memberDto, String title, String content, String image, String hashtag) {
        return new ArticleDto(null, boardId, memberDto, title, content, image, hashtag, null, null);
    }

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
    public Article toEntity(Member member, Board board) {
        return Article.of(
                member,
                board,
                title,
                content,
                image,
                hashtag
        );
    }
}
