package com.pinterest.repository;

import com.pinterest.domain.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
}
