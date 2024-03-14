package com.pinterest.service;

import com.pinterest.domain.Follow;
import com.pinterest.domain.Member;
import com.pinterest.dto.FollowDto;
import com.pinterest.repository.FollowRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.query.FollowQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final FollowQueryRepository followQueryRepository;

    public FollowDto getFollow(String fromMemberEmail, Long toMemberId) {
        Follow follow = followQueryRepository.getFollow(fromMemberEmail, toMemberId);
        if (follow != null) {
            return FollowDto.from(follow);
        }
        return null;
    }

    public Long countToMember(Long toMemberId) {
        Member toMember = memberRepository.findById(toMemberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));
        return followRepository.countByToMember(toMember);
    }

    public Long countFromMember(Long fromMemberId) {
        Member fromMember = memberRepository.findById(fromMemberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));
        return followRepository.countByFromMember(fromMember);
    }

    @Transactional
    public void createFollow(String fromMemberEmail, Long toMemberId) {
        Member fromMember = memberRepository.findByEmail(fromMemberEmail)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));
        Member toMember = memberRepository.findById(toMemberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));
        followRepository.save(Follow.of(fromMember, toMember));
    }

    @Transactional
    public void cancel(Long followId) {
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new EntityNotFoundException("팔로우 정보가 없습니다."));
        followRepository.delete(follow);
    }
}
