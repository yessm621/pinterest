package com.pinterest.repository.query;

import com.pinterest.domain.Article;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.pinterest.domain.QArticle.article;

@Repository
public class ArticleQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public ArticleQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Slice<Article> findArticles(String searchKeyword, Pageable pageable) {
        List<Article> articles = queryFactory
                .selectFrom(article)
                .where(article.title.contains(searchKeyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(articles, pageable, hasNextPage(articles, pageable.getPageSize()));
    }

    private boolean hasNextPage(List<Article> articles, int pageSize) {
        if (articles.size() > pageSize) {
            articles.remove(pageSize);
            return true;
        }
        return false;
    }
}
