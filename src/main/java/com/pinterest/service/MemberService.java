package com.pinterest.service;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.domain.Member;
import com.pinterest.domain.MemberRole;
import com.pinterest.dto.ProfileDto;
import com.pinterest.dto.request.JoinRequest;
import com.pinterest.error.PinterestException;
import com.pinterest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public CustomUserDetails loadUserByUsername(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email));

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getMemberRole().getKey()));

        return new CustomUserDetails(member.getEmail(), member.getPassword(), authorities, null);
    }

    @Transactional
    public void save(JoinRequest dto) {
        Member member = Member.of(
                dto.getEmail(),
                bCryptPasswordEncoder.encode(dto.getPassword()),
                MemberRole.USER
        );
        memberRepository.save(member);
    }

    public ProfileDto getMemberEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(ProfileDto::from)
                .orElseThrow(() -> new PinterestException("프로필이 없습니다."));
    }

    public ProfileDto getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .map(ProfileDto::from)
                .orElseThrow(() -> new PinterestException("프로필이 없습니다."));
    }
}
