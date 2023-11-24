package com.pinterest.dto.request;

import com.pinterest.dto.MemberDto;
import com.pinterest.dto.SubscribeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscribeRequest {

    private Long boardId;

    public static SubscribeRequest of(Long articleId) {
        return new SubscribeRequest(articleId);
    }

    public SubscribeDto toDto(MemberDto memberDto) {
        return SubscribeDto.of(boardId, memberDto);
    }
}
