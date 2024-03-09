package com.pinterest.service;

import com.pinterest.domain.*;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.ArticleLikeDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.repository.ArticleLikeRepository;
import com.pinterest.repository.ArticleRepository;
import com.pinterest.repository.BoardRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.query.ArticleLikeQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 핀 저장")
class ArticleLikeServiceTest {

    @InjectMocks
    private ArticleLikeService sut;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleLikeRepository articleLikeRepository;

    @Mock
    private ArticleLikeQueryRepository articleLikeQueryRepository;

    @Test
    @DisplayName("사용자가 저장한 핀 리스트를 조회한다.")
    void givenBoardIdAndEmail_whenSearchingArticles_thenReturnsArticles() {
        Long boardId = 1L;
        String email = "email";
        given(articleLikeQueryRepository.getArticleLikes(boardId, email)).willReturn(List.of(createArticle()));

        List<ArticleDto> articleLikes = sut.getArticleLikes(boardId, email);

        assertThat(articleLikes).isNotNull();
        then(articleLikeQueryRepository).should().getArticleLikes(boardId, email);
    }

    @Test
    @DisplayName("핀 저장 정보를 조회한다.")
    void givenArticleIdAndEmail_whenSearchingArticleLike_thenReturnsArticleLike() {
        given(articleLikeQueryRepository.getArticleLike(anyLong(), anyString()))
                .willReturn(createArticleLike());

        ArticleLikeDto articleLike = sut.getArticleLike(anyLong(), anyString());

        assertThat(articleLike).isNotNull();
        then(articleLikeQueryRepository).should().getArticleLike(anyLong(), anyString());
    }

    @Test
    @DisplayName("핀 저장 정보가 없을 경우 null 을 반환한다.")
    void givenArticleIdAndEmail_whenSearchingArticleLike_thenReturnsNull() {
        given(articleLikeQueryRepository.getArticleLike(anyLong(), anyString()))
                .willReturn(null);

        ArticleLikeDto articleLike = sut.getArticleLike(anyLong(), anyString());

        assertThat(articleLike).isNull();
        then(articleLikeQueryRepository).should().getArticleLike(anyLong(), anyString());
    }

    @Test
    @DisplayName("핀을 저장한다.")
    void givenArticleLikeDto_whenSavingArticleLike_thenReturnVoid() {
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(createMember()));
        given(boardRepository.findById(anyLong())).willReturn(Optional.of(createBoard()));
        given(articleRepository.findById(anyLong())).willReturn(Optional.of(createArticle()));

        sut.saveArticleLike(createArticleLikeDto());

        then(memberRepository).should().findByEmail(anyString());
        then(boardRepository).should().findById(anyLong());
        then(articleRepository).should().findById(anyLong());
    }

    @Test
    @DisplayName("핀 저장 시, 사용자 정보가 없으면 예외를 반환한다.")
    void givenArticleLikeDto_whenSavingArticleLikeNoneExistMember_thenThrowsException() {
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sut.saveArticleLike(createArticleLikeDto()));
        then(memberRepository).should().findByEmail(anyString());
    }

    @Test
    @DisplayName("핀 저장 시, 보드 정보가 없으면 예외를 반환한다.")
    void givenArticleLikeDto_whenSavingArticleLikeNoneExistBoard_thenThrowsException() {
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(createMember()));
        given(boardRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sut.saveArticleLike(createArticleLikeDto()));
        then(memberRepository).should().findByEmail(anyString());
        then(boardRepository).should().findById(anyLong());
    }

    @Test
    @DisplayName("핀 저장 시, 핀 정보가 없으면 예외를 반환한다.")
    void givenArticleLikeDto_whenSavingArticleLikeNoneExistArticle_thenThrowsException() {
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(createMember()));
        given(boardRepository.findById(anyLong())).willReturn(Optional.of(createBoard()));
        given(articleRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sut.saveArticleLike(createArticleLikeDto()));
        then(memberRepository).should().findByEmail(anyString());
        then(boardRepository).should().findById(anyLong());
        then(articleRepository).should().findById(anyLong());
    }

    @Test
    @DisplayName("핀 저장을 취소한다.")
    void givenArticleLikeId_whenDeletingArticleLike_thenReturnVoid() {
        given(articleLikeRepository.findById(anyLong())).willReturn(Optional.of(createArticleLike()));

        sut.deleteArticleLike(anyLong());

        then(articleLikeRepository).should().findById(anyLong());
    }

    @Test
    @DisplayName("핀 저장을 취소할 때, 핀 정보가 없으면 예외를 반환한다.")
    void givenArticleLikeId_whenDeletingArticleLikeNoneExistArticle_thenThrowsException() {
        given(articleLikeRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sut.deleteArticleLike(anyLong()));
        then(articleLikeRepository).should().findById(anyLong());
    }

    private Member createMember() {
        return Member.of(
                "yessm621@gmail.com",
                "test123",
                "yessm",
                "image"
        );
    }

    private Board createBoard() {
        return Board.of(
                createMember(),
                "board title"
        );
    }

    private Article createArticle() {
        return Article.of(
                createMember(),
                createBoard(),
                "article title",
                "article content",
                createFile(),
                "#hashtag"
        );
    }

    private FileEntity createFile() {
        return FileEntity.of(
                "fileName",
                "savedName",
                "savedPath"
        );
    }

    private ArticleLike createArticleLike() {
        return ArticleLike.of(
                createMember(),
                createBoard(),
                createArticle()
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

    private ArticleLikeDto createArticleLikeDto() {
        return ArticleLikeDto.of(
                createMemberDto(),
                1L,
                1L
        );
    }
}