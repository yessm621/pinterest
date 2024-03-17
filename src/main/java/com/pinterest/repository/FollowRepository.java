package com.pinterest.repository;

import com.pinterest.domain.Follow;
import com.pinterest.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Long countByFromMember(Member member);

    Long countByToMember(Member member);
}
