package com.pinterest.dto.request;

import com.pinterest.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberRequest {

    private String nickname;
    private String image;

    public static MemberRequest of(String nickname, String image) {
        return new MemberRequest(nickname, image);
    }

    public MemberDto toDto(String email) {
        return MemberDto.of(
                null,
                email,
                null,
                nickname,
                image,
                null,
                null
        );
    }
}
