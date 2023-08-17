package com.pinterest.service;

import com.pinterest.domain.Article;
import com.pinterest.domain.Member;
import com.pinterest.domain.Subscribe;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.SubscribeDto;
import com.pinterest.repository.ArticleRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;

    public List<ArticleDto> searchArticles(String email) {
        return articleRepository.findByMember_Email(email).stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveSubscribe(SubscribeDto dto) {
        Member member = memberRepository.findByEmail(dto.getMemberDto().getEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원이 없습니다."));
        Article article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new EntityNotFoundException("article이 없습니다."));
        Optional<Subscribe> findBySubscribe = subscribeRepository
                .findSubscribeByArticle_IdAndMember_id(article.getId(), member.getId());
        if (findBySubscribe.isEmpty()) {
            subscribeRepository.save(dto.toEntity(member, article));
        } else {
            subscribeRepository.deleteById(findBySubscribe.get().getId());
        }
    }

    @Transactional
    public void cancelSubscribe(Long subscribe_id) {
        subscribeRepository.deleteById(subscribe_id);
    }
}
