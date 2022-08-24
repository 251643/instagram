package com.example.intermediate.service;

import com.example.intermediate.dto.S3Dto;
import com.example.intermediate.dto.comment.CommentResponseDto;
import com.example.intermediate.dto.article.ArticleResponseDto;
import com.example.intermediate.dto.article.ArticleRequestDto;
import com.example.intermediate.dto.ResponseDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.model.*;
import com.example.intermediate.repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import com.example.intermediate.shared.Time;
import com.example.intermediate.shared.aws.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ArticleService {

  private final ArticleRepository articleRepository;
  private final MemberRepository memberRepository;
  private final HeartRepository heartRepository;
  private final ImageRepository imageRepository;
  private final DeletedUrlPathRepository deletedUrlPathRepository;
  private final TokenProvider tokenProvider;
  private final S3Uploader s3Uploader;


  @Transactional
  public ArticleResponseDto createArticle(ArticleRequestDto requestDto,
                                      UserDetailsImpl userDetailsImpl,
                                      List<MultipartFile> multipartFileList) throws IOException {
    if (multipartFileList.isEmpty())
      throw new IllegalArgumentException("이미지 파일을 넣어주세요.");
    if (requestDto.getContent() == null)
      throw new IllegalArgumentException("내용을 입력하세요");


    List<Image> imgList = new ArrayList<>();

    Article article = Article.builder()
            .content(requestDto.getContent())
            .member(userDetailsImpl.getMember())
            .isLike(false)
            .build();
    articleRepository.save(article);

    for (MultipartFile file : multipartFileList) {
      S3Dto s3Dto = s3Uploader.upload(file);
      Image image = Image.builder()
              .imgUrl(s3Dto.getUploadImageUrl())
              .urlPath(s3Dto.getFileName())
              .article(article)
              .build();
      imgList.add(image);
      imageRepository.save(image);
    }

    return ArticleResponseDto.builder()
            .id(article.getId())
            .content(article.getContent())
            .isLike(false)
            .imageList(imgList)
            .nickname(article.getMember().getNickname())
            .timeMsg(Time.convertLocaldatetimeToTime(article.getCreatedAt()))
            .heartCnt("0")
            .commentCnt("0")
            .build();
  }

  @Transactional(readOnly = true)
  public ArticleResponseDto getArticle(Long id, UserDetailsImpl userDetailsImpl) {
    Article article = isPresentArticle(id);
    if (null == article) {
      throw new RuntimeException("사용자를 찾을 수 없습니다");
    }

    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

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

    return ArticleResponseDto.builder()
            .id(article.getId())
            .content(article.getContent())
            .isLike(isPresentHeart(article, userDetailsImpl.getMember()))
            .commentResponseDtoList(commentResponseDtoList)
            .imageList(article.getImgUrlList())
            .nickname(article.getMember().getNickname())
            .timeMsg(Time.convertLocaldatetimeToTime(article.getCreatedAt()))
            .heartCnt(String.valueOf(article.getHeartList().size()))
            .commentCnt(String.valueOf(article.getComments().size()))
            .build();
  }

  @Transactional(readOnly = true)
  public List<ArticleResponseDto> getAllArticle(UserDetailsImpl userDetailsImpl) {

    List<Article> articleList = articleRepository.findAllByOrderByModifiedAtDesc();

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
    return articleResponseDtoList;
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
      throw new RuntimeException("Token이 유효하지 않습니다.");
    }

    Article article = isPresentArticle(id);
    if (null == article) {
      throw new RuntimeException("존재하지 않는 게시글 id 입니다.");
    }

    if (article.validateMember(member)) {
      throw new RuntimeException("작성자만 수정할 수 있습니다.");
    }

    article.update(requestDto);
    return ResponseDto.success(article);
  }

  @Transactional
  public Long deleteArticle(Long id, HttpServletRequest request) {


    Member member = validateMember(request);
    if (null == member) {
      throw new RuntimeException("Token이 유효하지 않습니다.");
    }

    Article article = isPresentArticle(id);
    if (null == article) {
      throw new RuntimeException("존재하지 않는 게시글 id 입니다.");
    }

    if (article.validateMember(member)) {
      throw new RuntimeException("작성자만 수정할 수 있습니다.");
    }

    List<Image> imageList = imageRepository.findAllByArticle(article);
    for (Image image : imageList) {
      DeletedUrlPath deletedUrlPath = new DeletedUrlPath();
      deletedUrlPath.setDeletedUrlPath(image.getUrlPath());

      deletedUrlPathRepository.save(deletedUrlPath);
      article.deleteImg(image);
    }

    articleRepository.delete(article);
    return id;
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

  public boolean isPresentHeart(Article article, Member member) {
    return heartRepository.existsByMemberAndArticle(member, article);
  }

  public void removeS3Image() {
    List<DeletedUrlPath> deletedUrlPathList = deletedUrlPathRepository.findAll();
    for (DeletedUrlPath deletedUrlPath : deletedUrlPathList) {
      s3Uploader.remove(deletedUrlPath.getDeletedUrlPath());
    }
    deletedUrlPathRepository.deleteAll();
  }
}
