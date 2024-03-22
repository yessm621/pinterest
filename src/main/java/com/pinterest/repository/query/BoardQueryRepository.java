package com.pinterest.repository.query;

import com.pinterest.domain.Board;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.pinterest.domain.QBoard.board;

@Repository
public class BoardQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public BoardQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Page<Board> searchBoards(String email, Pageable pageable) {
        List<Board> boards = queryFactory
                .selectFrom(board)
                .where(emailEq(email))
                .orderBy(board.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(board.count())
                .from(board)
                .where(emailEq(email))
                .fetchOne();

        return new PageImpl<>(boards, pageable, count);
    }

    private BooleanExpression emailEq(String emailCond) {
        return emailCond != null ? board.member.email.eq(emailCond) : null;
    }
}
