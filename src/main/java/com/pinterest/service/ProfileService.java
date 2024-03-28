package com.pinterest.service;

import com.pinterest.domain.FileEntity;
import com.pinterest.domain.Member;
import com.pinterest.dto.MemberDto;
import com.pinterest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProfileService {

    private final FileService fileService;
    private final MemberRepository memberRepository;

    @Transactional
    public void updateMember(Long profileId, MemberDto dto, MultipartFile file) {
        try {
            Member member = memberRepository.getReferenceById(profileId);

            if (member.getEmail().equals(dto.getEmail())) {
                if (file.isEmpty()) {
                    member.update(dto.getNickname());
                } else {
                    FileEntity fileEntity = fileService.upload(file);
                    member.update(dto.getNickname(), fileEntity);
                }
            }
        } catch (EntityNotFoundException e) {
            log.warn("프로필 업데이트 실패. 프로필을 찾을 수 없습니다.");
        }
    }
}
