package com.pinterest.service;

import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import com.pinterest.domain.Subscribe;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.dto.SubscribeDto;
import com.pinterest.repository.BoardRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.SubscribeRepository;
import com.pinterest.repository.query.SubscribeQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 구독 구현")
public class SubscribeServiceTest {

    @InjectMocks
    SubscribeService sut;

    @Mock
    SubscribeRepository subscribeRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    BoardRepository boardRepository;

    @Mock
    SubscribeQueryRepository subscribeQueryRepository;

    @Test
    @DisplayName("회원 이메일로 조회하면, 해당하는 구독 리스트를 반환한다.")
    void givenMemberId_whenSearchingBoards_thenReturnBoards() {
        // Given
        String email = "yessm621@gmail.com";
        given(subscribeQueryRepository.findSubscribeBoards(email)).willReturn(List.of(createBoard()));

        // When
        List<BoardDto> boards = sut.searchBoards(email);

        // Then
        then(subscribeQueryRepository).should().findSubscribeBoards(email);
        assertThat(boards).isNotEmpty();
    }

    @Test
    @DisplayName("구독을 하면, 회원과 board 정보를 저장한다.")
    void givenSubscribeInfo_whenSavingSubscribe_thenSavesMemberAndBoard() {
        // Given
        Subscribe subscribe = createSubscribe();
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(createMember()));
        given(boardRepository.findById(anyLong())).willReturn(Optional.of(createBoard()));

        // When
        sut.saveSubscribe(createSubscribeDto());

        // Then
        then(memberRepository).should().findByEmail(any());
        then(boardRepository).should().findById(anyLong());
        verify(subscribeRepository, times(1)).save(any());
        verify(subscribeRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("구독을 취소하면, 회원과 board 정보를 삭제한다.")
    void givenSubscribeInfo_whenDeletingSubscribe_thenDeletingMemberAndBoard() {
        // Given
        Long subscribe_id = 1L;
        willDoNothing().given(subscribeRepository).deleteById(subscribe_id);

        // When
        sut.cancelSubscribe(subscribe_id);

        // Then
        then(subscribeRepository).should().deleteById(subscribe_id);
    }

    private Subscribe createSubscribe() {
        return Subscribe.of(
                createMember(),
                createBoard()
        );
    }

    private SubscribeDto createSubscribeDto() {
        return SubscribeDto.of(
                1L,
                createMemberDto()
        );
    }

    private BoardDto createBoardDto() {
        return BoardDto.of(
                createMemberDto(),
                "board title",
                "board image"
        );
    }

    private Board createBoard() {
        return Board.of(
                createMember(),
                "board title",
                "board image"
        );
    }

    private Member createMember() {
        return Member.of(
                "yessm621@gmail.com",
                "test123",
                "yessm",
                "승미입니다.",
                "image"
        );
    }

    private MemberDto createMemberDto() {
        return MemberDto.of(
                1L,
                "yessm621@gmail.com",
                "test123",
                "yessm",
                "승미입니다.",
                "image",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
