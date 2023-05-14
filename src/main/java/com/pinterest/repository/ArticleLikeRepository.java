package com.pinterest.repository;

import com.pinterest.domain.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
}
