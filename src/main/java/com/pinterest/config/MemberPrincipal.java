package com.pinterest.config;

import com.pinterest.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@AllArgsConstructor
public class MemberPrincipal implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private String nickname;
    private String description;

    public static MemberPrincipal of(String username, String password, String nickname, String description) {
        Set<RoleType> roleTypes = Set.of(RoleType.USER);

        return new MemberPrincipal(
                username,
                password,
                roleTypes.stream()
                        .map(RoleType::getName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toUnmodifiableSet()),
                nickname,
                description
        );
    }

    public static MemberPrincipal from(MemberDto dto) {
        return MemberPrincipal.of(
                dto.getEmail(),
                dto.getPassword(),
                dto.getNickname(),
                dto.getDescription()
        );
    }

    public MemberDto toDto() {
        return MemberDto.of(
                username,
                password,
                nickname,
                description
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum RoleType {
        USER("ROLE_USER");

        @Getter
        private final String name;

        RoleType(String name) {
            this.name = name;
        }
    }
}
