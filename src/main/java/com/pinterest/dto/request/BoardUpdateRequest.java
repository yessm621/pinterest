package com.pinterest.dto.request;

import com.pinterest.dto.BoardDto;
import com.pinterest.dto.MemberDto;
import lombok.Data;

@Data
public class BoardUpdateRequest {

    private String boardTitle;

    private BoardUpdateRequest(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public static BoardUpdateRequest of(String boardTitle) {
        return new BoardUpdateRequest(boardTitle);
    }

    public BoardDto toDto(MemberDto memberDto) {
        return BoardDto.of(
                memberDto,
                boardTitle
        );
    }
}
