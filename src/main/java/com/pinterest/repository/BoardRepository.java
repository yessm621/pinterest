package com.pinterest.repository;

import com.pinterest.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface BoardRepository extends JpaRepository<Board, Long> {
}
