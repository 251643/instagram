package com.example.intermediate.service;

import com.example.intermediate.dto.ResponseDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.model.Article;
import com.example.intermediate.model.Comment;
import com.example.intermediate.model.Heart;
import com.example.intermediate.model.Member;
import com.example.intermediate.repository.ArticleRepository;
import com.example.intermediate.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final TokenProvider tokenProvider;
    private final ArticleRepository articleRepository;
    private final HeartRepository heartRepository;
    public ResponseDto<?> addHeart(Long id, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        Article article = isPresentArticle(id);
        if (null == article) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }
        Heart heart = isPresentHeart(member, article);
        if (null == heart) {
            heartRepository.save(
                    Heart.builder()
                            .member(member)
                            .article(article)
                            .build()
            );
            return ResponseDto.success("like success");
        } else {
            heartRepository.delete(heart);
            return ResponseDto.success("cancel like success");
        }
    }

    @Transactional(readOnly = true)
    public Article isPresentArticle(Long id) {
        Optional<Article> optionalArticle= articleRepository.findById(id);
        return optionalArticle.orElse(null);
    }

    @Transactional(readOnly = true)
    public Heart isPresentHeart(Member member, Article article) {
        Optional<Heart> optionalHeart = heartRepository.findByMemberAndArticle(member, article);
        return optionalHeart.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

}
