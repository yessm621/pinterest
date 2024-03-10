package com.pinterest.dto;

import com.pinterest.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private Long id;
    private Long fileId;
    private String email;
    private String nickname;
    private String image;

    // entity -> dto
    public static ProfileDto from(Member entity) {
        return new ProfileDto(
                entity.getId(),
                entity.getFile() == null ? null : entity.getFile().getId(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getImage()
        );
    }
}
