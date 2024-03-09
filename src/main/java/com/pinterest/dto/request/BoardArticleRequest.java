package com.pinterest.dto.request;

import com.pinterest.dto.BoardDto;
import com.pinterest.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardArticleRequest {

    private String boardTitle;

    public static BoardArticleRequest of(String title) {
        return new BoardArticleRequest(title);
    }

    public BoardDto toDto(MemberDto memberDto) {
        return BoardDto.of(
                memberDto,
                boardTitle
        );
    }
}
