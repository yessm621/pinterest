package com.pinterest.dto;

import com.pinterest.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String description;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static MemberDto of(Long id, String email, String password, String nickname, String description, String image, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new MemberDto(id, email, password, nickname, description, image, createdAt, modifiedAt);
    }

    // entity -> dto
    public static MemberDto from(Member entity) {
        return new MemberDto(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getNickname(),
                entity.getDescription(),
                entity.getImage(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }

    // dto -> entity
    public Member toEntity() {
        return Member.of(
                email,
                password,
                nickname,
                description,
                image
        );
    }
}
