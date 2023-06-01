package com.pinterest.domain;

import lombok.Getter;

public enum SearchType {

    TITLE("제목"),
    HASHTAG("해시태그");

    @Getter
    private final String description;

    SearchType(String description) {
        this.description = description;
    }
}
