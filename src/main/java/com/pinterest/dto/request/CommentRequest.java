package com.pinterest.dto.request;

import com.pinterest.dto.CommentDto;
import com.pinterest.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentRequest {
    private Long articleId;
    private String content;


    public static CommentRequest of(Long articleId, String content) {
        return new CommentRequest(articleId, content);
    }

    public CommentDto toDto(MemberDto memberDto) {
        return CommentDto.of(articleId, memberDto, content);
    }
}
