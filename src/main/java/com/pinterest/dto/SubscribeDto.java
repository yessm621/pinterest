package com.pinterest.dto;

import com.pinterest.domain.Article;
import com.pinterest.domain.Member;
import com.pinterest.domain.Subscribe;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscribeDto {

    private Long id;
    private Long articleId;
    private MemberDto memberDto;

    public static SubscribeDto of(Long articleId, MemberDto memberDto) {
        return new SubscribeDto(null, articleId, memberDto);
    }

    public static SubscribeDto from(Subscribe entity) {
        return new SubscribeDto(
                entity.getId(),
                entity.getArticle().getId(),
                MemberDto.from(entity.getMember())
        );
    }

    public Subscribe toEntity(Member member, Article article) {
        return Subscribe.of(
                member,
                article
        );
    }
}
