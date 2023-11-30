package com.pinterest.dto;

import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import com.pinterest.domain.Subscribe;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscribeDto {

    private Long id;
    private Long boardId;
    private MemberDto memberDto;

    public static SubscribeDto of(Long boardId, MemberDto memberDto) {
        return new SubscribeDto(null, boardId, memberDto);
    }

    public static SubscribeDto from(Subscribe entity) {
        return new SubscribeDto(
                entity.getId(),
                entity.getBoard().getId(),
                MemberDto.from(entity.getMember())
        );
    }

    public Subscribe toEntity(Member member, Board board) {
        return Subscribe.of(
                member,
                board
        );
    }
}
