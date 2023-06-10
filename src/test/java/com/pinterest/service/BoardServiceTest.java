package com.pinterest.service;

import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.BoardWithArticleDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.repository.BoardRepository;
import com.pinterest.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
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
    private MemberRepository memberRepository;

    @Test
    @DisplayName("검색어 없이 보드를 검색하면, 보드 리스트를 반환한다.")
    void givenNoSearchParameters_whenSearchingBoard_thenReturnsBoards() {
        // Given
        Pageable pageable = Pageable.ofSize(16);
        given(boardRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        Page<BoardDto> boards = sut.searchBoards(null, pageable);

        // Then
        assertThat(boards).isNotNull();
        assertThat(boards).isEmpty();
        then(boardRepository).should().findAll(pageable);
    }

    @Test
    @DisplayName("검색어와 함께 보드를 검색하면, 보드 리스트를 반환한다.")
    void givenSearchParameters_whenSearchingBoard_thenReturnsBoards() {
        // Given
        String searchKeyword = "search keyword";
        Pageable pageable = Pageable.ofSize(16);
        given(boardRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<BoardDto> boards = sut.searchBoards(searchKeyword, pageable);

        // Then
        assertThat(boards).isNotNull();
        then(boardRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @Test
    @DisplayName("보드를 조회하면, 보드를 반환한다.")
    void givenBoardId_whenSearchingBoard_thenReturnsBoard() {
        // Given
        Long boardId = 1L;
        Board board = createBoard();
        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));

        // When
        BoardWithArticleDto dto = sut.getBoardWithArticles(boardId);

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
        assertThrows(EntityNotFoundException.class, () -> sut.getBoardWithArticles(boardId));
        then(boardRepository).should().findById(boardId);
    }

    @Test
    @DisplayName("보드 정보를 입력하면, 보드를 생성한다.")
    void givenBoardInfo_whenSavingBoard_thenSavesBoard() throws Exception {
        // Given
        BoardDto dto = createBoardDto("title", "image");
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
        BoardDto dto = createBoardDto("title", "image");
        given(boardRepository.getReferenceById(dto.getId())).willReturn(board);
        given(memberRepository.findByEmail(dto.getMemberDto().getEmail()))
                .willReturn(Optional.of(dto.getMemberDto().toEntity()));

        // When
        sut.updateBoard(dto.getId(), dto);

        // Then
        assertThat(board).hasFieldOrPropertyWithValue("title", dto.getTitle());
        assertThat(board).hasFieldOrPropertyWithValue("image", dto.getImage());
        then(boardRepository).should().getReferenceById(dto.getId());
        then(memberRepository).should().findByEmail(dto.getMemberDto().getEmail());
    }

    @Test
    @DisplayName("없는 보드의 수정 정보를 입력하면, 경고 로그를 찍고 아무것도 하지 않는다.")
    void givenNoneExistentBoardInfo_whenUpdatingBoard_thenLogsWarningAndDoesNothing() throws Exception {
        // Given
        BoardDto dto = createBoardDto("new title", "new image");
        given(boardRepository.getReferenceById(dto.getId())).willThrow(EntityNotFoundException.class);

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
                "title",
                "image"
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

    private BoardDto createBoardDto(String title, String image) {
        return BoardDto.of(
                1L,
                createMemberDto(),
                title,
                image,
                LocalDateTime.now(),
                LocalDateTime.now()
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
