package com.pinterest.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@ToString
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Column(nullable = false, length = 255)
    private String title;
    @Column(nullable = false, length = 2000)
    private String content;
    @Column(nullable = false, length = 2000)
    private String image;
    @Column(length = 100)
    private String hashtag;

    protected Article() {
    }

    private Article(Member member, Board board, String title, String content, String image, String hashtag) {
        this.member = member;
        this.board = board;
        this.title = title;
        this.content = content;
        this.image = image;
        this.hashtag = hashtag;
    }

    public static Article of(Member member, Board board, String title, String content, String image, String hashtag) {
        return new Article(member, board, title, content, image, hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article)) return false;
        Article article = (Article) o;
        return id != null && Objects.equals(getId(), article.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
