package com.pinterest.dto;

import com.pinterest.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleWithCommentDto {

    private Long id;
    private Long fileId;
    private String savedName;
    private MemberDto memberDto;
    private ArrayList<CommentDto> commentDtoList;
    private String title;
    private String content;
    private String hashtag;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ArticleWithCommentDto of(Long id, Long fileId, String savedName, MemberDto memberDto, ArrayList<CommentDto> commentDtoList, String title, String content, String hashtag, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new ArticleWithCommentDto(id, fileId, savedName, memberDto, commentDtoList, title, content, hashtag, createdAt, modifiedAt);
    }

    // entity -> dto
    public static ArticleWithCommentDto from(Article entity) {
        return new ArticleWithCommentDto(
                entity.getId(),
                entity.getFile() == null ? null : entity.getFile().getId(),
                entity.getFile() == null ? null : entity.getFile().getSavedName(),
                MemberDto.from(entity.getMember()),
                entity.getComments().stream()
                        .map(CommentDto::from)
                        .collect(Collectors.toCollection(ArrayList::new)),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
