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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
    @DisplayName("댓글 정보를 입력하면, 댓글을 저장한다.")
    void givenCommentInfo_whenSavingComment_thenSavesComment() {
        // Given
        CommentDto dto = createCommentDto("comment");
        given(articleRepository.getReferenceById(dto.getArticleId())).willReturn(createArticle());
        given(memberRepository.findByEmail(dto.getMemberDto().getEmail())).willReturn(Optional.of(createMember()));
        given(commentRepository.save(any(Comment.class))).willReturn(null);

        // When
        sut.saveComment(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.getArticleId());
        then(memberRepository).should().findByEmail(dto.getMemberDto().getEmail());
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

    private CommentDto createCommentDto(String content) {
        return CommentDto.of(
                1L,
                1L,
                createMemberDto(),
                content,
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

    private Article createArticle() {
        return Article.of(
                createMember(),
                createBoard(),
                "article title",
                "article content",
                null,
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
}
