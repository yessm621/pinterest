package com.pinterest.repository;

import com.pinterest.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ArticleRepository extends JpaRepository<Article, Long> {

    void deleteByIdAndMember_Email(Long articleId, String email);

    List<Article> findByMember_Email(String email);
}
