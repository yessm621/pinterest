package com.pinterest.repository;

import com.pinterest.domain.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    Optional<Subscribe> findSubscribeByBoard_IdAndMember_id(Long boardId, Long memberId);

    Optional<Subscribe> findSubscribeByBoard_IdAndMember_Email(Long boardId, String email);
}
