package com.pinterest.repository.query;

import com.pinterest.domain.Article;
import com.pinterest.domain.ArticleLike;
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
                .where(articleLike.board.id.eq(boardId), articleLike.member.email.eq(email))
                .fetch();
    }

    public ArticleLike getArticleLike(Long articleId, String email) {
        return queryFactory
                .selectFrom(articleLike)
                .where(articleLike.article.id.eq(articleId), articleLike.member.email.eq(email))
                .fetchOne();
    }
}
