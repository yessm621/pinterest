package com.pinterest.service;

import com.pinterest.domain.Article;
import com.pinterest.domain.Board;
import com.pinterest.domain.FileEntity;
import com.pinterest.domain.Member;
import com.pinterest.dto.ArticleDto;
import com.pinterest.dto.ArticleWithCommentDto;
import com.pinterest.repository.ArticleRepository;
import com.pinterest.repository.BoardRepository;
import com.pinterest.repository.MemberRepository;
import com.pinterest.repository.query.ArticleQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ArticleService {

    private final BoardRepository boardRepository;
    private final ArticleRepository articleRepository;
    private final ArticleQueryRepository articleQueryRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    public Page<ArticleDto> searchArticles(String searchKeyword, Pageable pageable) {
        return articleQueryRepository.searchArticles(searchKeyword, pageable)
                .map(ArticleDto::from);
    }

    public List<ArticleDto> getArticles(String email) {
        return articleRepository.findByMember_Email(email).stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    public ArticleWithCommentDto getArticleWithComment(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentDto::from)
                .orElseThrow(() -> new EntityNotFoundException("핀이 없습니다."));
    }

    @Transactional
    public void saveArticle(MultipartFile file, ArticleDto dto) {
        Member member = memberRepository.findByEmail(dto.getMemberDto().getEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));

        FileEntity fileEntity = fileService.upload(file);

        Board board = boardRepository.getReferenceById(dto.getBoardId());
        articleRepository.save(dto.toEntity(member, board, fileEntity));
    }

    @Transactional
    public void deleteArticle(Long articleId, String email) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("핀이 없습니다."));
        fileService.deleteImage(article.getFile().getSavedName());
        articleRepository.delete(article);
    }
}
