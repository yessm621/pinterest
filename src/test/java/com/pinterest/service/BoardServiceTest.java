package com.pinterest.service;

import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.error.PinterestException;
import com.pinterest.repository.BoardRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.query.BoardQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 보드")
class BoardServiceTest {

    @InjectMocks
    private BoardService sut;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardQueryRepository boardQueryRepository;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("보드 리스트를 반환한다.")
    void givenNoSearchParameters_whenSearchingBoard_thenReturnsBoards() {
        // Given
        String email = "email";
        Pageable pageable = Pageable.ofSize(16);
        given(boardQueryRepository.searchBoards(email, pageable)).willReturn(Page.empty());

        // When
        Page<BoardDto> boards = sut.searchBoards(email, pageable);

        // Then
        assertThat(boards).isNotNull();
        assertThat(boards).isEmpty();
        then(boardQueryRepository).should().searchBoards(email, pageable);
    }

    @Test
    @DisplayName("이메일로 보드 리스트를 조회한다.")
    void givenEmail_whenSearchingBoards_thenReturnsBoards() {
        // Given
        String email = "test@gmail.com";
        Board board = createBoard();
        given(boardRepository.findByMember_Email(email)).willReturn(List.of(board));

        // When
        List<BoardDto> dto = sut.getBoards(email);

        // Then
        then(boardRepository).should().findByMember_Email(email);
    }

    @Test
    @DisplayName("보드를 조회하면, 보드를 반환한다.")
    void givenBoardId_whenSearchingBoard_thenReturnsBoard() {
        // Given
        Long boardId = 1L;
        Board board = createBoard();
        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));

        // When
        BoardDto dto = sut.getBoard(boardId);

        // Then
        assertThat(dto).hasFieldOrPropertyWithValue("title", board.getTitle());
        then(boardRepository).should().findById(boardId);
    }

    @Test
    @DisplayName("없는 보드를 조회하면, 예외를 던진다.")
    void givenNoneExistentBoardId_whenSearchingBoard_thenThrowsException() throws Exception {
        // Given
        Long boardId = 0L;
        given(boardRepository.findById(boardId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(PinterestException.class, () -> sut.getBoard(boardId));
        then(boardRepository).should().findById(boardId);
    }

    @Test
    @DisplayName("보드 정보를 입력하면, 보드를 생성한다.")
    void givenBoardInfo_whenSavingBoard_thenSavesBoard() throws Exception {
        // Given
        BoardDto dto = createBoardDto("title");
        given(memberRepository.findByEmail(dto.getMemberDto().getEmail())).willReturn(Optional.of(createMember()));
        given(boardRepository.save(any(Board.class))).willReturn(createBoard());

        // When
        sut.saveBoard(dto);

        // Then
        then(memberRepository).should().findByEmail(dto.getMemberDto().getEmail());
        then(boardRepository).should().save(any(Board.class));
    }

    @Test
    @DisplayName("보드 수정 정보를 입력하면, 보드를 수정한다.")
    void givenModifiedBoardInfo_whenUpdatingBoard_thenUpdatesBoard() throws Exception {
        // Given
        Board board = createBoard();
        BoardDto dto = createBoardDto("title");
        given(boardRepository.getReferenceById(dto.getId())).willReturn(board);
        given(memberRepository.findByEmail(dto.getMemberDto().getEmail()))
                .willReturn(Optional.of(dto.getMemberDto().toEntity()));

        // When
        sut.updateBoard(dto.getId(), dto);

        // Then
        assertThat(board).hasFieldOrPropertyWithValue("title", dto.getTitle());
        then(boardRepository).should().getReferenceById(dto.getId());
        then(memberRepository).should().findByEmail(dto.getMemberDto().getEmail());
    }

    @Test
    @DisplayName("없는 보드의 수정 정보를 입력하면, 경고 로그를 찍고 아무것도 하지 않는다.")
    void givenNoneExistentBoardInfo_whenUpdatingBoard_thenLogsWarningAndDoesNothing() throws Exception {
        // Given
        BoardDto dto = createBoardDto("new title");
        given(boardRepository.getReferenceById(dto.getId())).willThrow(PinterestException.class);

        // When
        sut.updateBoard(dto.getId(), dto);

        // Then
        then(boardRepository).should().getReferenceById(dto.getId());
    }

    @Test
    @DisplayName("보드의 ID를 입력하면, 보드를 삭제한다.")
    void givenBoardId_whenDeletingBoard_thenDeletesBoard() throws Exception {
        // Given
        Long boardId = 1L;
        String email = "test@gmail.com";
        willDoNothing().given(boardRepository).deleteByIdAndMember_Email(boardId, email);

        // When
        sut.deleteBoard(boardId, email);

        // Then
        then(boardRepository).should().deleteByIdAndMember_Email(boardId, email);
    }

    private Board createBoard() {
        return Board.of(
                createMember(),
                "title"
        );
    }

    private Member createMember() {
        return Member.of(
                "yessm621@gmail.com",
                "test123",
                "yessm",
                "image"
        );
    }

    private BoardDto createBoardDto(String title) {
        return BoardDto.of(
                1L,
                createMemberDto(),
                title,
                LocalDateTime.now()
        );
    }

    private MemberDto createMemberDto() {
        return MemberDto.of(
                1L,
                "yessm621@gmail.com",
                "test123",
                "yessm",
                "image",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
