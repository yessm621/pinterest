package com.pinterest.repository;

import com.pinterest.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByMember_Email(String email);

    void deleteByIdAndMember_Email(Long boardId, String email);
}
