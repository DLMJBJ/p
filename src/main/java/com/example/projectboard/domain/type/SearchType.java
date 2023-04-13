package com.example.projectboard.domain.type;

import lombok.Getter;

public enum SearchType {
    TITLE("제목"),
    CONTENT("본문"),
    HASHTAG("해시태그"),
    ID("유저 ID"),
    NICKNAME("닉내임");

    @Getter
    private final String description;
    SearchType(String description) {
        this.description = description;
    }

}
