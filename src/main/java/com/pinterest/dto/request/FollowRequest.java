package com.pinterest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowRequest {

    private Long toMemberId;
    private Long articleId;

    public static FollowRequest of(Long toMemberId, Long articleId) {
        return new FollowRequest(toMemberId, articleId);
    }
}
