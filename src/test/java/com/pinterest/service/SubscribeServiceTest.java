package com.pinterest.service;

import com.pinterest.domain.Article;
import com.pinterest.domain.Board;
import com.pinterest.domain.Member;
import com.pinterest.domain.Subscribe;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.MemberDto;
import com.pinterest.dto.SubscribeDto;
import com.pinterest.repository.ArticleRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.SubscribeRepository;
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
    ArticleRepository articleRepository;

    @Test
    @DisplayName("회원 이메일로 조회하면, 해당하는 구독 리스트를 반환한다.")
    void givenMemberId_whenSearchingArticles_thenReturnArticles() {
        // Given
        String email = "yessm621@gmail.com";
        given(articleRepository.findByMember_Email(email)).willReturn(List.of(createArticle()));

        // When
        List<ArticleDto> articles = sut.searchArticles(email);

        // Then
        then(articleRepository).should().findByMember_Email(email);
        assertThat(articles).isNotEmpty();
    }

    @Test
    @DisplayName("구독을 하면, 회원과 article 정보를 저장한다.")
    void givenSubscribeInfo_whenSavingSubscribe_thenSavesMemberAndArticle() {
        // Given
        Subscribe subscribe = createSubscribe();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(articleRepository.findById(anyLong())).willReturn(Optional.of(createArticle()));
        given(subscribeRepository.save(any(Subscribe.class))).willReturn(subscribe);

        // When
        sut.saveSubscribe(createSubscribeDto());

        // Then
        then(memberRepository).should().findById(anyLong());
        then(articleRepository).should().findById(anyLong());
        then(subscribeRepository).should().save(any(Subscribe.class));
    }

    @Test
    @DisplayName("구독을 취소하면, 회원과 article 정보를 삭제한다.")
    void givenSubscribeInfo_whenDeletingSubscribe_thenDeletingMemberAndArticle() {
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
                createArticle()
        );
    }

    private SubscribeDto createSubscribeDto() {
        return SubscribeDto.of(
                1L,
                1L
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
