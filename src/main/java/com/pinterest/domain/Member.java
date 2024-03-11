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
    @Column(length = 255)
    private String password;

    @Setter
    @Column(length = 100)
    private String nickname;

    @Setter
    @Column(length = 2000)
    private String image;

    @OneToOne
    @JoinColumn(name = "file_id")
    private FileEntity file;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole = MemberRole.USER;

    private String provider;

    protected Member() {
    }

    private Member(String email, String password, String nickname, String image, MemberRole memberRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.image = image;
        this.memberRole = memberRole;
    }

    public Member update(String nickname, String image, String provider) {
        this.nickname = nickname;
        this.image = image;
        this.provider = provider;
        return this;
    }

    public Member update(String nickname, FileEntity file) {
        this.nickname = nickname;
        this.file = file;
        return this;
    }

    public Member update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getMemberRoleKey() {
        return this.memberRole.getKey();
    }

    public static Member of(String email, String password, String nickname, String image) {
        return new Member(email, password, nickname, image, null);
    }

    public static Member of(String email, String password, MemberRole memberRole) {
        return new Member(email, password, null, null, memberRole);
    }

    public static Member of(String email, String nickname, String image, MemberRole memberRole) {
        return new Member(email, null, nickname, image, memberRole);
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
