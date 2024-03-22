package com.pinterest.dto.request;

import lombok.Data;

@Data
public class FollowProfileRequest {

    private Long toMemberId;
    private String email;
    private String type;
}
