package com.pinterest.dto;

import com.pinterest.domain.Article;
import com.pinterest.domain.Board;
import com.pinterest.domain.FileEntity;
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
    private String hashtag;
    private String image;
    private LocalDateTime modifiedAt;

    public static ArticleDto of(Long boardId, MemberDto memberDto, String title, String content, String hashtag) {
        return new ArticleDto(null, boardId, memberDto, title, content, hashtag, null, null);
    }

    // entity -> dto
    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                entity.getBoard().getId(),
                MemberDto.from(entity.getMember()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getFile().getSavedName(),
                entity.getModifiedAt()
        );
    }

    // dto -> entity
    public Article toEntity(Member member, Board board, FileEntity file) {
        return Article.of(
                member,
                board,
                title,
                content,
                file,
                hashtag
        );
    }
}
