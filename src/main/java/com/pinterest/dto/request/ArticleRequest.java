package com.pinterest.dto.request;

import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleRequest {

    private Long boardId;
    private String title;
    private String content;
    private String image;
    private String hashtag;

    public static ArticleRequest of(Long boardId, String title, String content, String image, String hashtag) {
        return new ArticleRequest(boardId, title, content, image, hashtag);
    }

    public ArticleDto toDto(MemberDto memberDto) {
        return ArticleDto.of(
                boardId,
                memberDto,
                title,
                content,
                image,
                hashtag
        );
    }
}
