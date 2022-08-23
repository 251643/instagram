package com.example.intermediate.service;


import com.example.intermediate.dto.ResponseDto;
import com.example.intermediate.dto.article.ArticleResponseDto;
import com.example.intermediate.dto.article.ImageResponseDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.model.Article;
import com.example.intermediate.model.Heart;
import com.example.intermediate.model.Member;
import com.example.intermediate.model.UserDetailsImpl;
import com.example.intermediate.repository.ArticleRepository;
import com.example.intermediate.repository.HeartRepository;
import com.example.intermediate.repository.MemberRepository;
import com.example.intermediate.shared.Time;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final ArticleRepository articleRepository;
    private final HeartRepository heartRepository;
    @Transactional(readOnly = true)
    public ResponseDto<?> getMyArticles(UserDetailsImpl userDetailsImpl) {

        List<Article> articleList = articleRepository.findAllByMemberOrderByModifiedAtDesc(userDetailsImpl.getMember());

        List<ArticleResponseDto> articleResponseDtoList = new ArrayList<>();
        for (Article article : articleList) {
            articleResponseDtoList.add(
                    ArticleResponseDto.builder()
                            .id(article.getId())
                            .content(article.getContent())
                            .isLike(isPresentHeart(article, userDetailsImpl.getMember()))
                            .nickname(article.getMember().getNickname())
                            .timeMsg(Time.convertLocaldatetimeToTime(article.getCreatedAt()))
                            .imageList(article.getImgUrlList())
                            .heartCnt(String.valueOf(article.getHeartList().size()))
                            .commentCnt(String.valueOf(article.getComments().size()))
                            .build()
            );
        }
        return ResponseDto.success(articleResponseDtoList);
    }


    @Transactional(readOnly = true)
    public ResponseDto<?> getMyHeartArticles(UserDetailsImpl userDetailsImpl) {

        List<Heart> HeartList = heartRepository.findAllByMember(userDetailsImpl.getMember());

        List<ArticleResponseDto> articleResponseDtoList = new ArrayList<>();
        for (Heart heart : HeartList) {
            Article article = heart.getArticle();
            articleResponseDtoList.add(
                    ArticleResponseDto.builder()
                            .id(article.getId())
                            .content(article.getContent())
                            .isLike(isPresentHeart(article, userDetailsImpl.getMember()))
                            .nickname(article.getMember().getNickname())
                            .timeMsg(Time.convertLocaldatetimeToTime(article.getCreatedAt()))
                            .imageList(article.getImgUrlList())
                            .heartCnt(String.valueOf(article.getHeartList().size()))
                            .commentCnt(String.valueOf(article.getComments().size()))
                            .build()
            );
        }
        return ResponseDto.success(articleResponseDtoList);
    }

    public boolean isPresentHeart(Article article, Member member) {
        return heartRepository.existsByMemberAndArticle(member, article);
    }
}
