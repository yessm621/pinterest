package com.pinterest.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@ToString(exclude = {"member", "article"})
public class Subscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscribe_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Article article;

    protected Subscribe() {
    }

    private Subscribe(Member member, Article article) {
        this.member = member;
        this.article = article;
    }

    public static Subscribe of(Member member, Article article) {
        return new Subscribe(member, article);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscribe)) return false;
        Subscribe subscribe = (Subscribe) o;
        return id != null && Objects.equals(getId(), subscribe.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
