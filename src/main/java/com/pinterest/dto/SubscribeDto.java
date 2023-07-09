package com.pinterest.dto;

import com.pinterest.domain.Article;
import com.pinterest.domain.Member;
import com.pinterest.domain.Subscribe;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscribeDto {

    private Long memberId;
    private Long articleId;

    public static SubscribeDto of(Long memberId, Long articleId) {
        return new SubscribeDto(memberId, articleId);
    }

    public static SubscribeDto from(Member member, Article article) {
        return new SubscribeDto(
                member.getId(),
                article.getId()
        );
    }

    public Subscribe toEntity(Member member, Article article) {
        return Subscribe.of(
                member,
                article
        );
    }
}
