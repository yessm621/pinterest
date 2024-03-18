package com.pinterest.repository;

import com.pinterest.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByArticle_Id(Long articleId);

    void deleteByIdAndMember_Email(Long commentId, String email);
}
