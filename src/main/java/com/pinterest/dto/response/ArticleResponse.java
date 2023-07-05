package com.pinterest.dto.response;

import com.pinterest.dto.ArticleDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleResponse {

    private Long id;
    private Long boardId;
    private String title;
    private String content;
    private String image;
    private String hashtag;

    public static ArticleResponse of(Long id, Long boardId, String title, String content, String image, String hashtag) {
        return new ArticleResponse(id, boardId, title, content, image, hashtag);
    }

    public static ArticleResponse from(ArticleDto dto) {
        return new ArticleResponse(
                dto.getId(),
                dto.getBoardId(),
                dto.getTitle(),
                dto.getContent(),
                dto.getImage(),
                dto.getHashtag()
        );
    }
}
