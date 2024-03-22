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
    private String savedName;
    private String email;
    private String nickname;
    private String image;

    public static ProfileDto of(Long id, String savedName, String email, String nickname, String image) {
        return new ProfileDto(id, savedName, email, nickname, image);
    }

    // entity -> dto
    public static ProfileDto from(Member entity) {
        return new ProfileDto(
                entity.getId(),
                entity.getFile() == null ? null : entity.getFile().getSavedName(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getImage()
        );
    }
}
