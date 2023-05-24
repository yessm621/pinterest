package com.pinterest.repository;

import com.pinterest.domain.Comment;
import com.pinterest.domain.QComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment>,
        QuerydslBinderCustomizer<QComment> {

    List<Comment> findByArticle_Id(Long articleId);

    @Override
    default void customize(QuerydslBindings bindings, QComment root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.content, root.createdAt);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
    }
}
