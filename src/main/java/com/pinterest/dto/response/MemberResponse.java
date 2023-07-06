package com.pinterest.dto.response;

import com.pinterest.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String nickname;
    private String description;
    private String image;

    public static MemberResponse of(Long id, String nickname, String description, String image) {
        return new MemberResponse(id, nickname, description, image);
    }

    public static MemberResponse from(MemberDto dto) {
        return new MemberResponse(
                dto.getId(),
                dto.getNickname(),
                dto.getDescription(),
                dto.getImage()
        );
    }
}
