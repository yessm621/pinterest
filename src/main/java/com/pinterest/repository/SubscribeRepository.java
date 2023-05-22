package com.pinterest.repository;

import com.pinterest.domain.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
}
