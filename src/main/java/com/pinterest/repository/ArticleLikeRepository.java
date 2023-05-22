package com.pinterest.repository;

import com.pinterest.domain.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
}
