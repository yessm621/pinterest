package com.pinterest.dto.response;

import com.pinterest.dto.ArticleDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleResponse {

    private Long id;
    private Long boardId;
    private Long fileId;
    private String title;
    private String content;
    private String hashtag;

    public static ArticleResponse of(Long id, Long boardId, Long fileId, String title, String content, String hashtag) {
        return new ArticleResponse(id, boardId, fileId, title, content, hashtag);
    }

    public static ArticleResponse from(ArticleDto dto) {
        return new ArticleResponse(
                dto.getId(),
                dto.getBoardId(),
                dto.getFileId(),
                dto.getTitle(),
                dto.getContent(),
                dto.getHashtag()
        );
    }
}
