package com.pinterest.repository.query;

import com.pinterest.domain.Board;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.pinterest.domain.QBoard.board;
import static com.pinterest.domain.QSubscribe.subscribe;

@Repository
public class SubscribeQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public SubscribeQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<Board> findSubscribeBoards(String email) {
        return queryFactory
                .selectFrom(board)
                .join(board.subscribes, subscribe)
                .where(
                        subscribe.member.email.eq(email)
                )
                .fetch();
    }
}
