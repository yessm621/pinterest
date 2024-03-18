package com.pinterest.repository.query;

import com.pinterest.domain.Article;
import com.pinterest.domain.ArticleLike;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.pinterest.domain.QArticleLike.articleLike;

@Repository
public class ArticleLikeQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public ArticleLikeQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<Article> getArticleLikes(Long boardId, String email) {
        return queryFactory
                .select(articleLike.article)
                .from(articleLike)
                .where(boardIdEq(boardId), emailEq(email))
                .fetch();
    }

    public ArticleLike getArticleLike(Long articleId, String email) {
        return queryFactory
                .selectFrom(articleLike)
                .where(articleIdEq(articleId), emailEq(email))
                .fetchOne();
    }

    private BooleanExpression boardIdEq(Long boardIdCond) {
        return boardIdCond != null ? articleLike.board.id.eq(boardIdCond) : null;
    }

    private BooleanExpression articleIdEq(Long articleIdCond) {
        return articleIdCond != null ? articleLike.article.id.eq(articleIdCond) : null;
    }

    private BooleanExpression emailEq(String emailCond) {
        return emailCond != null ? articleLike.member.email.eq(emailCond) : null;
    }
}
