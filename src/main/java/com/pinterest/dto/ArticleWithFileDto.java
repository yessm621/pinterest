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
public class ArticleWithFileDto {

    private Long id;
    private Long boardId;
    private Long fileId;
    private String savedName;
    private MemberDto memberDto;
    private String title;
    private String content;
    private String hashtag;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    // entity -> dto
    public static ArticleWithFileDto from(Article entity) {
        return new ArticleWithFileDto(
                entity.getId(),
                entity.getBoard().getId(),
                entity.getFile().getId(),
                entity.getFile().getSavedName(),
                MemberDto.from(entity.getMember()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
