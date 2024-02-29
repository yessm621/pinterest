package com.pinterest.repository;

import com.pinterest.domain.Article;
import com.pinterest.domain.QArticle;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByTitleContaining(String searchKeyword, Pageable pageable);

    Page<Article> findByHashtag(String searchKeyword, Pageable pageable);

    void deleteByIdAndMember_Email(Long articleId, String email);

    List<Article> findByMember_Email(String email);

    List<Article> findByMember_Id(Long memberId);
}
