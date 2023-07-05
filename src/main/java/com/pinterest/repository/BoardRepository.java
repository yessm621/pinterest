package com.pinterest.repository;

import com.pinterest.domain.Board;
import com.pinterest.domain.QBoard;
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
public interface BoardRepository extends JpaRepository<Board, Long>, QuerydslPredicateExecutor<Board>,
        QuerydslBinderCustomizer<QBoard> {

    Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);

    List<Board> findByMember_Email(String email);

    void deleteByIdAndMember_Email(Long boardId, String email);

    @Override
    default void customize(QuerydslBindings bindings, QBoard root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.image, root.createdAt);
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
    }
}
