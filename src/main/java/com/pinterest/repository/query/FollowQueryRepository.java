package com.pinterest.repository.query;

import com.pinterest.domain.Follow;
import com.pinterest.domain.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

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

    public List<Member> getFollowers(Long toMemberId) {
        return queryFactory
                .select(follow.fromMember)
                .from(follow)
                .where(follow.toMember.id.eq(toMemberId))
                .fetch();
    }

    public List<Member> getFollowings(Long fromMemberId) {
        return queryFactory
                .select(follow.toMember)
                .from(follow)
                .where(follow.fromMember.id.eq(fromMemberId))
                .fetch();
    }
}
