package com.pinterest.repository.query;

import com.pinterest.domain.Follow;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import static com.pinterest.domain.QFollow.follow;

@Repository
public class FollowQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public FollowQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Follow getFollow(String fromMemberEmail, Long toMemberId) {
        return queryFactory
                .selectFrom(follow)
                .where(follow.fromMember.email.eq(fromMemberEmail), follow.toMember.id.eq(toMemberId))
                .fetchOne();
    }
}
