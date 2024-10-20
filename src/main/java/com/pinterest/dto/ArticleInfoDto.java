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
public class ArticleInfoDto {

    private Long id;
    private String title;
    private String image;

    // entity -> dto
    public static ArticleInfoDto from(Article entity) {
        return new ArticleInfoDto(
                entity.getId(),
                entity.getTitle(),
                entity.getFile().getSavedName()
        );
    }
}
