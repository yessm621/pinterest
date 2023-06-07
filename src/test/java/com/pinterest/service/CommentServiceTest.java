package com.pinterest.service;

import com.pinterest.domain.Article;
import com.pinterest.domain.Board;
import com.pinterest.domain.Comment;
import com.pinterest.domain.Member;
import com.pinterest.dto.CommentDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.repository.ArticleRepository;
import com.pinterest.repository.CommentRepository;
import com.pinterest.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 댓글 구현")
class CommentServiceTest {

    @InjectMocks
    CommentService sut;

    @Mock
    ArticleRepository articleRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    CommentRepository commentRepository;

    @Test
    @DisplayName("게시글 ID로 조회하면, 해당하는 댓글 리스트를 반환한다.")
    void givenArticleId_whenSearchingComments_thenReturnComments() {
        // Given
        Long articleId = 1L;
        Comment comment = createComment("content");
        given(commentRepository.findByArticle_Id(articleId)).willReturn(List.of(comment));

        // When
        List<CommentDto> dto = sut.searchComment(articleId);

        // Then
        assertThat(dto)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("content", comment.getContent());
        then(commentRepository).should().findByArticle_Id(articleId);
    }

    @Test
    @DisplayName("댓글 정보를 입력하면, 댓글을 저장한다.")
    void givenCommentInfo_whenSavingComment_thenSavesComment() {
        // Given
        CommentDto dto = createCommentDto("comment");
        given(articleRepository.getReferenceById(dto.getArticleId())).willReturn(createArticle());
        given(memberRepository.getReferenceById(dto.getMemberDto().getId())).willReturn(createMember());
        given(commentRepository.save(any(Comment.class))).willReturn(null);

        // When
        sut.saveComment(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.getArticleId());
        then(memberRepository).should().getReferenceById(dto.getMemberDto().getId());
        then(commentRepository).should().save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 저장을 시도했는데 맞는 article 이 없으면, 경고 로그를 찍고 아무것도 안한다.")
    void givenNoneExistentArticle_whenSavingComment_thenLogsWarningAndDoesNothing() {
        // Given
        CommentDto dto = createCommentDto("comment");
        given(articleRepository.getReferenceById(dto.getArticleId())).willThrow(EntityNotFoundException.class);

        // When
        sut.saveComment(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.getArticleId());
        then(commentRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("댓글 수정 정보를 입력하면, 댓글을 수정한다.")
    void givenCommentInfo_whenUpdatingComment_thenUpdatesComment() {
        // Given
        String oldContent = "old comment";
        String updatedContent = "new comment";
        Comment comment = createComment(oldContent);
        CommentDto dto = createCommentDto(updatedContent);
        given(commentRepository.getReferenceById(dto.getId())).willReturn(comment);

        // When
        sut.updateComment(dto);

        // Then
        assertThat(comment.getContent())
                .isNotEqualTo(oldContent)
                .isEqualTo(updatedContent);
        then(commentRepository).should().getReferenceById(dto.getId());
    }

    @Test
    @DisplayName("없는 댓글 정보를 수정하려고 하면, 경고 로그를 찍고 아무것도 안한다.")
    void givenNoneExistentComment_whenUpdatingComment_thenLogsWarningAndDoesNoting() {
        // Given
        CommentDto dto = createCommentDto("comment");
        given(commentRepository.getReferenceById(dto.getId())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateComment(dto);

        // Then
        then(commentRepository).should().getReferenceById(dto.getId());
    }

    @Test
    @DisplayName("댓글 ID를 입력하면, 댓글을 삭제한다.")
    void givenArticleId_whenDeletingComment_thenDeletesComment() {
        // Given
        Long commentId = 1L;
        willDoNothing().given(commentRepository).deleteById(commentId);

        // When
        sut.deleteComment(commentId);

        // Then
        then(commentRepository).should().deleteById(commentId);
    }

    private CommentDto createCommentDto(String content) {
        return CommentDto.of(
                1L,
                1L,
                createMemberDto(),
                content,
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

    private Comment createComment(String content) {
        return Comment.of(
                createMember(),
                Article.of(createMember(), createBoard(), "title", "content", "image", "hashtag"),
                content
        );
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
}
