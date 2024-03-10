package com.pinterest.service;

import com.pinterest.config.CustomUserDetails;
import com.pinterest.domain.FileEntity;
import com.pinterest.domain.Member;
import com.pinterest.domain.MemberRole;
import com.pinterest.dto.MemberDto;
import com.pinterest.dto.ProfileDto;
import com.pinterest.dto.request.JoinRequest;
import com.pinterest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService implements UserDetailsService {

    private final FileService fileService;
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
                .orElseThrow(() -> new EntityNotFoundException("프로필이 없습니다."));
    }

    public ProfileDto getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .map(ProfileDto::from)
                .orElseThrow(() -> new EntityNotFoundException("프로필이 없습니다."));
    }

    @Transactional
    public void updateMember(Long profileId, MemberDto dto, MultipartFile file) {
        try {
            Member member = memberRepository.getReferenceById(profileId);

            if (member.getEmail().equals(dto.getEmail())) {
                if (file.isEmpty()) {
                    member.update(dto.getNickname());
                } else {
                    FileEntity fileEntity = fileService.saveFile(file);
                    member.update(dto.getNickname(), fileEntity);
                }
            }
        } catch (EntityNotFoundException e) {
            log.warn("프로필 업데이트 실패. 프로필을 찾을 수 없습니다.");
        }
    }
}
