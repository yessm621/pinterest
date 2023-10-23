package com.pinterest.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(indexes = {
        @Index(columnList = "email", unique = true)
})
@Getter
@ToString
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;
    @Column(nullable = false, length = 255)
    private String password;

    @Setter
    @Column(length = 100)
    private String nickname;

    @Setter
    @Column(length = 2000)
    private String description;

    @Setter
    @Column(length = 2000)
    private String image;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole = MemberRole.USER;

    protected Member() {
    }

    private Member(String email, String password, String nickname, String description, String image) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.description = description;
        this.image = image;
    }

    public static Member of(String email, String password, String nickname, String description, String image) {
        return new Member(email, password, nickname, description, image);
    }

    public static Member of(String email, String password) {
        return new Member(email, password, null, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return id != null && Objects.equals(getId(), member.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
