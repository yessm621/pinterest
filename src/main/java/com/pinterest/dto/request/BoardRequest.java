package com.pinterest.dto.request;

import com.pinterest.dto.BoardDto;
import com.pinterest.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardRequest {

    private String title;
    private String image;

    public static BoardRequest of(String title, String image) {
        return new BoardRequest(title, image);
    }

    public BoardDto toDto(MemberDto memberDto) {
        return BoardDto.of(
                memberDto,
                title,
                image
        );
    }
}
