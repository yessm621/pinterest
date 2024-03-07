package com.pinterest.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ArticleLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    protected ArticleLike() {
    }

    private ArticleLike(Member member, Board board, Article article) {
        this.member = member;
        this.board = board;
        this.article = article;
    }

    public static ArticleLike of(Member member, Board board, Article article) {
        return new ArticleLike(member, board, article);
    }
}
