package com.pinterest.dto.request;

import com.pinterest.dto.BoardDto;
import com.pinterest.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardCreateRequest {

    private String boardTitle;
    private String type;

    private BoardCreateRequest(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public static BoardCreateRequest of(String boardTitle) {
        return new BoardCreateRequest(boardTitle);
    }

    public static BoardCreateRequest of(String boardTitle, String type) {
        return new BoardCreateRequest(boardTitle, type);
    }

    public BoardDto toDto(MemberDto memberDto) {
        return BoardDto.of(
                memberDto,
                boardTitle
        );
    }
}
