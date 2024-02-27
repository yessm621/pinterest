package com.pinterest.dto;

import com.pinterest.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardWithArticleDto {

    private Long id;
    private MemberDto memberDto;
    private ArrayList<ArticleDto> articleDtoList;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static BoardWithArticleDto of(Long id, MemberDto memberDto, ArrayList<ArticleDto> articleDtoList, String title, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new BoardWithArticleDto(id, memberDto, articleDtoList, title, createdAt, modifiedAt);
    }

    // entity -> dto
    public static BoardWithArticleDto from(Board entity) {
        return new BoardWithArticleDto(
                entity.getId(),
                MemberDto.from(entity.getMember()),
                entity.getArticles().stream()
                        .map(ArticleDto::from)
                        .collect(Collectors.toCollection(ArrayList::new)),
                entity.getTitle(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
