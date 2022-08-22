package com.example.intermediate.service;

import com.example.intermediate.dto.article.ImageResponseDto;
import com.example.intermediate.dto.comment.CommentResponseDto;
import com.example.intermediate.dto.article.ArticleResponseDto;
import com.example.intermediate.model.*;
import com.example.intermediate.dto.article.ArticleRequestDto;
import com.example.intermediate.dto.ResponseDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.ArticleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import com.example.intermediate.repository.HeartRepository;
import com.example.intermediate.repository.MemberRepository;
import com.example.intermediate.shared.Time;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {

  private final ArticleRepository articleRepository;
  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;
  private final HeartRepository heartRepository;
  private final TokenProvider tokenProvider;


  @Transactional
  public ResponseDto<?> createArticle(ArticleRequestDto requestDto, HttpServletRequest request) {
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

    Article article = Article.builder()
        .content(requestDto.getContent())
        .member(member)
        .isLike(false)
        .build();
    articleRepository.save(article);
    return ResponseDto.success(
        ArticleResponseDto.builder()
            .id(article.getId())
            .content(article.getContent())
            .nickname(article.getMember().getNickname())
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getArticle(Long id) {
    Article article = isPresentArticle(id);
    if (null == article) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
    List<ImageResponseDto> imageResponseDtoList = new ArrayList<>();

    for (Comment comment : article.getComments()) {
      commentResponseDtoList.add(
          CommentResponseDto.builder()
              .id(comment.getId())
              .nickname(comment.getMember().getNickname())
              .content(comment.getContent())
              .timeMsg(Time.convertLocaldatetimeToTime(comment.getCreatedAt()))
              .build()
      );
    }

    for (Image image : article.getImgUrlList()) {
      imageResponseDtoList.add(
          ImageResponseDto.builder()
            .id(image.getId())
            .urlPath(image.getUrlPath())
            .imgUrl(image.getImgUrl())
            .build()
      );

    }

    return ResponseDto.success(
        ArticleResponseDto.builder()
            .id(article.getId())
            .content(article.getContent())
            .isLike(isPresentHeart(article))
            .commentResponseDtoList(commentResponseDtoList)
            .imageResponseDtoList(imageResponseDtoList)
            .nickname(article.getMember().getNickname())
            .timeMsg(Time.convertLocaldatetimeToTime(article.getCreatedAt()))
            .heartCnt(String.valueOf(article.getHeartList().size()))
            .commentCnt(String.valueOf(article.getComments().size()))
            .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllArticle() {

    List<Article> articleList = articleRepository.findAllByOrderByModifiedAtDesc();

    List<ImageResponseDto> imageResponseDtoList = new ArrayList<>();

    List<ArticleResponseDto> articleResponseDtoList = new ArrayList<>();
    for (Article article : articleList) {
      articleResponseDtoList.add(
              ArticleResponseDto.builder()
                      .id(article.getId())
                      .content(article.getContent())
                      .isLike(isPresentHeart(article))
                      .nickname(article.getMember().getNickname())
                      .timeMsg(Time.convertLocaldatetimeToTime(article.getCreatedAt()))
                      .imageResponseDtoList(imageResponseDtoList)
                      .heartCnt(String.valueOf(article.getHeartList().size()))
                      .commentCnt(String.valueOf(article.getComments().size()))
                      .build()
      );
    }
    return ResponseDto.success(articleResponseDtoList);
  }

  @Transactional
  public ResponseDto<Article> updateArticle(Long id, ArticleRequestDto requestDto, HttpServletRequest request) {
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

    if (article.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    article.update(requestDto);
    return ResponseDto.success(article);
  }

  @Transactional
  public ResponseDto<?> deleteArticle(Long id, HttpServletRequest request) {
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

    if (article.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }

    articleRepository.delete(article);
    return ResponseDto.success("delete success");
  }

  @Transactional(readOnly = true)
  public Article isPresentArticle(Long id) {
    Optional<Article> optionalPost = articleRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

  public boolean isPresentHeart(Article article) {
    return heartRepository.existsByMemberAndArticle(isPresentMember(), article);
  }

  @Transactional(readOnly = true)
  public Member isPresentMember() {
    Optional<Member> optionalMember = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    return optionalMember.orElse(null);
  }

}
