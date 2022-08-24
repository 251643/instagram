package com.example.intermediate.controller;

import com.example.intermediate.dto.article.ArticleRequestDto;
import com.example.intermediate.dto.article.ArticleResponseDto;
import com.example.intermediate.model.UserDetailsImpl;
import com.example.intermediate.service.ArticleService;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ArticleController {

  private final ArticleService articleService;

  @PostMapping(value = "/api/auth/article", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public ArticleResponseDto createArticle(@RequestPart(value="dto") ArticleRequestDto requestDto, // (required = false) 하면 value 타입 안정해도 가능
                                          @RequestPart(required = false, value="files") List<MultipartFile> multipartFileList,
                                          @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) throws IOException {
       return articleService.createArticle(requestDto, userDetailsImpl, multipartFileList);
  }

  @RequestMapping(value = "/api/auth/article/{id}", method = RequestMethod.GET)
  public ArticleResponseDto getArticle(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
    return articleService.getArticle(id, userDetailsImpl);
  }

  @RequestMapping(value = "/api/auth/article", method = RequestMethod.GET)
  public List<ArticleResponseDto> getAllArticles(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
    return articleService.getAllArticle(userDetailsImpl);
  }

//  @RequestMapping(value = "/api/auth/article/{id}", method = RequestMethod.PUT)
//  public ResponseDto<?> updateArticle(@PathVariable Long id, @RequestBody ArticleRequestDto articleRequestDto,
//      HttpServletRequest request) {
//    return articleService.updateArticle(id, articleRequestDto, request);
//  }

  @RequestMapping(value = "/api/auth/article/{id}", method = RequestMethod.DELETE)
  public Long deleteArticle(@PathVariable Long id,
      HttpServletRequest request) {
    return articleService.deleteArticle(id, request);
  }

}
