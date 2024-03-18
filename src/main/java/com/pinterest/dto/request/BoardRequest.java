package com.pinterest.dto.request;

import com.pinterest.dto.BoardDto;
import com.pinterest.dto.MemberDto;
import lombok.Data;

@Data
public class BoardRequest {

    private String boardTitle;
    private String type;

    private BoardRequest(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public static BoardRequest of(String boardTitle) {
        return new BoardRequest(boardTitle);
    }

    public BoardDto toDto(MemberDto memberDto) {
        return BoardDto.of(
                memberDto,
                boardTitle
        );
    }
}
