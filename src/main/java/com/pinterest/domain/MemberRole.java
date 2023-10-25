package com.pinterest.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {

    GUEST("ROLE_GUEST"),
    USER("ROLE_USER");

    private final String key;
}
