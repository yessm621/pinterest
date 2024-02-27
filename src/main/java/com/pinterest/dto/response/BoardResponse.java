package com.pinterest.dto.response;

import com.pinterest.dto.BoardDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BoardResponse {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private String email;
    private String nickname;

    public static BoardResponse of(Long id, String title, LocalDateTime createdAt, String email, String nickname) {
        return new BoardResponse(id, title, createdAt, email, nickname);
    }

    public static BoardResponse from(BoardDto dto) {
        String nickname = dto.getMemberDto().getNickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.getMemberDto().getEmail();
        }

        return new BoardResponse(
                dto.getId(),
                dto.getTitle(),
                dto.getCreatedAt(),
                dto.getMemberDto().getEmail(),
                nickname
        );
    }
}
