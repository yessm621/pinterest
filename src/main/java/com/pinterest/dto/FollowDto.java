package com.pinterest.dto;

import com.pinterest.domain.Follow;
import com.pinterest.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowDto {

    private Long id;
    private MemberDto fromMemberDto;
    private MemberDto toMemberDto;

    public static FollowDto of(Long id, MemberDto fromMember, MemberDto toMember) {
        return new FollowDto(id, fromMember, toMember);
    }

    public static FollowDto from(Follow entity) {
        return new FollowDto(
                entity.getId(),
                MemberDto.from(entity.getFromMember()),
                MemberDto.from(entity.getToMember())
        );
    }

    // dto -> entity
    public Follow toEntity(Member fromMember, Member toMember) {
        return Follow.of(
                fromMember,
                toMember
        );
    }
}
