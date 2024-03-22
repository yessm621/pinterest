package com.pinterest.repository.query;

import com.pinterest.domain.Follow;
import com.pinterest.domain.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
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
                .where(emailEq(fromMemberEmail), toMemberIdEq(toMemberId))
                .fetchOne();
    }

    public List<Member> getFollowers(Long toMemberId) {
        return queryFactory
                .select(follow.fromMember)
                .from(follow)
                .where(toMemberIdEq(toMemberId))
                .fetch();
    }

    public List<Member> getFollowings(Long fromMemberId) {
        return queryFactory
                .select(follow.toMember)
                .from(follow)
                .where(fromMemberIdEq(fromMemberId))
                .fetch();
    }

    private BooleanExpression emailEq(String emailCond) {
        return emailCond != null ? follow.fromMember.email.eq(emailCond) : null;
    }

    private BooleanExpression toMemberIdEq(Long toMemberIdCond) {
        return toMemberIdCond != null ? follow.toMember.id.eq(toMemberIdCond) : null;
    }

    private BooleanExpression fromMemberIdEq(Long fromMemberIdCond) {
        return fromMemberIdCond != null ? follow.fromMember.id.eq(fromMemberIdCond) : null;
    }
}
