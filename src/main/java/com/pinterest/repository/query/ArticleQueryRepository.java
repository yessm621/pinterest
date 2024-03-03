package com.pinterest.repository.query;

import com.pinterest.domain.Article;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.pinterest.domain.QArticle.article;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class ArticleQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public ArticleQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Page<Article> searchArticles(String searchKeyword, Pageable pageable) {
        List<Article> articles = queryFactory
                .selectFrom(article)
                .where(search(searchKeyword))
                .orderBy(article.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(article.count())
                .from(article)
                .where(search(searchKeyword))
                .fetchOne();

        return new PageImpl<>(articles, pageable, count);
    }

    private BooleanExpression search(@Nullable String searchKeyword) {
        return hasText(searchKeyword) ? article.title.contains(searchKeyword) : null;
    }
}
