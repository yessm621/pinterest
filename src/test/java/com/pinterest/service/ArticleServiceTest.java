package com.pinterest.service;

import com.pinterest.domain.Article;
import com.pinterest.domain.Board;
import com.pinterest.domain.FileEntity;
import com.pinterest.domain.Member;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.ArticleWithCommentDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.error.PinterestException;
import com.pinterest.repository.ArticleRepository;
import com.pinterest.repository.BoardRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.query.ArticleQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
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
    ArticleQueryRepository articleQueryRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    FileService fileService;

    @Test
    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 리스트를 반환한다.")
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticles() {
        // Given
        Pageable pageable = Pageable.ofSize(16);
        given(articleQueryRepository.searchArticles(null, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(null, pageable);

        // Then
        assertThat(articles).isNotNull();
        assertThat(articles).isEmpty();
        then(articleQueryRepository).should().searchArticles(null, pageable);
    }

    @Test
    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 리스트를 반환한다.")
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticles() {
        // Given
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(16);
        given(articleQueryRepository.searchArticles(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleQueryRepository).should().searchArticles(searchKeyword, pageable);
    }

    @Test
    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    void givenEmail_whenSearchingArticle_thenReturnsArticle() {
        // Given
        Article article = createArticle();
        given(articleRepository.findByMember_Email(anyString())).willReturn(List.of(article));

        // When
        List<ArticleDto> articles = sut.getArticles(anyString());

        // Then
        then(articleRepository).should().findByMember_Email(anyString());
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
        assertThrows(PinterestException.class, () -> sut.getArticleWithComment(articleId));
        then(articleRepository).should().findById(articleId);
    }

    @Test
    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        // Given
        ArticleDto dto = createArticleDto("title", "content", "hashtag");
        MockMultipartFile file = new MockMultipartFile("fileName", "test.png", "image/*", "test file".getBytes(StandardCharsets.UTF_8));

        given(boardRepository.getReferenceById(dto.getBoardId())).willReturn(createBoard());
        given(memberRepository.findByEmail(dto.getMemberDto().getEmail())).willReturn(Optional.of(createMember()));
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());
        given(fileService.upload(file)).willReturn(createFile());

        // When
        sut.saveArticle(file, dto);

        // Then
        then(boardRepository).should().getReferenceById(dto.getBoardId());
        then(memberRepository).should().findByEmail(dto.getMemberDto().getEmail());
        then(articleRepository).should().save(any(Article.class));
        then(fileService).should().upload(file);
    }

    @Test
    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다.")
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // Given
        Long articleId = 1L;
        String email = "test@gmail.com";
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        willDoNothing().given(fileService).deleteImage(anyString());
        willDoNothing().given(articleRepository).delete(article);

        // When
        sut.deleteArticle(articleId, email);

        // Then
        then(articleRepository).should().findById(articleId);
        then(fileService).should().deleteImage(anyString());
        then(articleRepository).should().delete(article);
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

    private Board createBoard() {
        return Board.of(
                createMember(),
                "board title"
        );
    }

    private Member createMember() {
        return Member.of(
                "test@gmail.com",
                "test123",
                "test",
                "image"
        );
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(
                1L,
                createMemberDto(),
                title,
                content,
                hashtag
        );
    }

    private MemberDto createMemberDto() {
        return MemberDto.of(
                1L,
                "test@gmail.com",
                "test123",
                "test",
                "image",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
