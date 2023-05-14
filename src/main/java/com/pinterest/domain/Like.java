package com.pinterest.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@ToString
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    protected Like() {
    }

    private Like(Member member, Article article) {
        this.member = member;
        this.article = article;
    }

    public static Like of(Member member, Article article) {
        return new Like(member, article);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Like)) return false;
        Like like = (Like) o;
        return id != null && Objects.equals(getId(), like.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
