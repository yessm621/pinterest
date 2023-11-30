package com.pinterest.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@ToString(exclude = "member")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Article> articles = new ArrayList<>();

    @Setter
    @Column(nullable = false, length = 255)
    private String title;

    @Setter
    @Column(length = 2000)
    private String image;

    @OneToMany(mappedBy = "board")
    private List<Subscribe> subscribes = new ArrayList<>();

    protected Board() {
    }

    private Board(Member member, String title, String image) {
        this.member = member;
        this.title = title;
        this.image = image;
    }

    public static Board of(Member member, String title, String image) {
        return new Board(member, title, image);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board)) return false;
        Board board = (Board) o;
        return id != null && Objects.equals(getId(), board.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
