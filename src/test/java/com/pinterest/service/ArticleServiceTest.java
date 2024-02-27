package com.pinterest.service;

import com.pinterest.domain.Article;
import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import com.pinterest.domain.SearchType;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.ArticleWithCommentDto;
import com.pinterest.dto.BoardDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.repository.ArticleRepository;
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
@DisplayName("비즈니스 로직 - article")
class ArticleServiceTest {

    @InjectMocks
    ArticleService sut;

    @Mock
    BoardRepository boardRepository;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 리스트를 반환한다.")
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticles() {
        // Given
        Pageable pageable = Pageable.ofSize(16);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        // Then
        assertThat(articles).isNotNull();
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @Test
    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 리스트를 반환한다.")
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticles() {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(16);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @Test
    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleWithCommentDto dto = sut.getArticleWithComment(articleId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @Test
    @DisplayName("없는 게시글을 조회하면, 예외를 던진다.")
    void givenNoneExistentArticleId_whenSearchingArticle_thenThrowsException() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> sut.getArticleWithComment(articleId));
        then(articleRepository).should().findById(articleId);
    }

    @Test
    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        // Given
        ArticleDto dto = createArticleDto("title", "content", "image", "hashtag");
        given(boardRepository.getReferenceById(dto.getBoardId())).willReturn(createBoard());
        given(memberRepository.findByEmail(dto.getMemberDto().getEmail())).willReturn(Optional.of(createMember()));
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        // When
        sut.saveArticle(dto);

        // Then
        then(boardRepository).should().getReferenceById(dto.getBoardId());
        then(memberRepository).should().findByEmail(dto.getMemberDto().getEmail());
        then(articleRepository).should().save(any(Article.class));
    }

    @Test
    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    void givenModifiedArticleInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("new title", "new content", "new image", "new hashtag");
        given(articleRepository.getReferenceById(dto.getId())).willReturn(article);
        given(memberRepository.findByEmail(dto.getMemberDto().getEmail()))
                .willReturn(Optional.of(dto.getMemberDto().toEntity()));

        // When
        sut.updateArticle(dto.getId(), dto);

        // Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.getTitle())
                .hasFieldOrPropertyWithValue("content", dto.getContent())
                .hasFieldOrPropertyWithValue("image", dto.getImage())
                .hasFieldOrPropertyWithValue("hashtag", dto.getHashtag());
        then(articleRepository).should().getReferenceById(dto.getId());
        then(memberRepository).should().findByEmail(dto.getMemberDto().getEmail());
    }

    @Test
    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    void givenNoneExistentArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
        // Given
        ArticleDto dto = createArticleDto("new title", "new content", "new image", "new hashtag");
        given(articleRepository.getReferenceById(dto.getId())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateArticle(dto.getId(), dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.getId());
    }

    @Test
    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다.")
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // Given
        Long articleId = 1L;
        String email = "test@gmail.com";
        willDoNothing().given(articleRepository).deleteByIdAndMember_Email(articleId, email);

        // When
        sut.deleteArticle(articleId, email);

        // Then
        then(articleRepository).should().deleteByIdAndMember_Email(articleId, email);
    }

    private Article createArticle() {
        return Article.of(
                createMember(),
                createBoard(),
                "article title",
                "article content",
                "article image",
                "#hashtag"
        );
    }

    private Board createBoard() {
        return Board.of(
                createMember(),
                "board title"
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
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private ArticleDto createArticleDto(String title, String content, String image, String hashtag) {
        return ArticleDto.of(
                1L,
                1L,
                createMemberDto(),
                title,
                content,
                image,
                hashtag,
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
                "image",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
