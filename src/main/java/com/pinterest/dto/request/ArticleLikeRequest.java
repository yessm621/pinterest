package com.pinterest.dto.request;

import com.pinterest.dto.ArticleLikeDto;
import com.pinterest.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleLikeRequest {

    private Long boardId;
    private Long articleId;

    public ArticleLikeDto toDto(MemberDto memberDto) {
        return ArticleLikeDto.of(
                memberDto,
                boardId,
                articleId
        );
    }
}
