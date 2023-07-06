package com.pinterest.service;

import com.pinterest.domain.Member;
import com.pinterest.dto.MemberDto;
import com.pinterest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberDto getMemberEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberDto::from)
                .orElseThrow(() -> new EntityNotFoundException("프로필이 없습니다."));
    }

    public MemberDto getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .map(MemberDto::from)
                .orElseThrow(() -> new EntityNotFoundException("프로필이 없습니다."));
    }

    @Transactional
    public void updateMember(Long profileId, MemberDto dto) {
        try {
            Member member = memberRepository.getReferenceById(profileId);

            if (member.getEmail().equals(dto.getEmail())) {
                if (dto.getNickname() != null) {
                    member.setNickname(dto.getNickname());
                }
                if (dto.getDescription() != null) {
                    member.setDescription(dto.getDescription());
                }
                if (dto.getImage() != null) {
                    member.setImage(dto.getImage());
                }
            }
        } catch (EntityNotFoundException e) {
            log.warn("프로필 업데이트 실패. 프로필을 찾을 수 없습니다.");
        }
    }
}
