package com.pinterest.dto.response;

import com.pinterest.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String email;
    private String nickname;
    private String image;

    public static MemberResponse of(Long id, String email, String nickname, String image) {
        return new MemberResponse(id, email, nickname, image);
    }

    public static MemberResponse from(MemberDto dto) {
        return new MemberResponse(
                dto.getId(),
                dto.getEmail(),
                dto.getNickname(),
                dto.getImage()
        );
    }
}
