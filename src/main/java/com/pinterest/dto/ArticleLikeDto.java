package com.pinterest.dto;

import com.pinterest.domain.Article;
import com.pinterest.domain.ArticleLike;
import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleLikeDto {

    private Long articleLikeId;
    private MemberDto memberDto;
    private Long boardId;
    private String boardTitle;
    private Long articleId;

    public static ArticleLikeDto of(MemberDto memberDto, Long boardId, Long articleId) {
        return new ArticleLikeDto(null, memberDto, boardId, null, articleId);
    }

    public static ArticleLikeDto of(Long articleLikeId, MemberDto memberDto, Long boardId, String boardTitle, Long articleId) {
        return new ArticleLikeDto(articleLikeId, memberDto, boardId, boardTitle, articleId);
    }

    // entity -> dto
    public static ArticleLikeDto from(ArticleLike entity) {
        return new ArticleLikeDto(
                entity.getId(),
                MemberDto.from(entity.getMember()),
                entity.getBoard().getId(),
                entity.getBoard().getTitle(),
                entity.getArticle().getId()
        );
    }

    // dto -> entity
    public ArticleLike toEntity(Member member, Board board, Article article) {
        return ArticleLike.of(
                member,
                board,
                article
        );
    }
}
