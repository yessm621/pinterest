package com.pinterest.dto;

import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {

    private Long id;
    private MemberDto memberDto;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static BoardDto of(MemberDto memberDto, String title) {
        return new BoardDto(null, memberDto, title, null, null);
    }

    public static BoardDto of(Long id, MemberDto memberDto, String title, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new BoardDto(id, memberDto, title, createdAt, modifiedAt);
    }

    // entity -> dto
    public static BoardDto from(Board entity) {
        return new BoardDto(
                entity.getId(),
                MemberDto.from(entity.getMember()),
                entity.getTitle(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }

    // dto -> entity
    public Board toEntity(Member member) {
        return Board.of(
                member,
                title
        );
    }
}
