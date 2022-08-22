package com.example.intermediate.controller;

import com.example.intermediate.dto.ResponseDto;
import com.example.intermediate.dto.article.ArticleRequestDto;
import com.example.intermediate.service.ArticleService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ArticleController {

  private final ArticleService articleService;

  @RequestMapping(value = "/api/auth/article", method = RequestMethod.POST)
  public ResponseDto<?> createArticle(@RequestBody ArticleRequestDto requestDto,
      HttpServletRequest request) {
    return articleService.createArticle(requestDto, request);
  }

  @RequestMapping(value = "/api/auth/article/{id}", method = RequestMethod.GET)
  public ResponseDto<?> getArticle(@PathVariable Long id) {
    return articleService.getArticle(id);
  }

  @RequestMapping(value = "/api/auth/article", method = RequestMethod.GET)
  public ResponseDto<?> getAllArticles() {
    return articleService.getAllArticle();
  }

  @RequestMapping(value = "/api/auth/article/{id}", method = RequestMethod.PUT)
  public ResponseDto<?> updateArticle(@PathVariable Long id, @RequestBody ArticleRequestDto articleRequestDto,
      HttpServletRequest request) {
    return articleService.updateArticle(id, articleRequestDto, request);
  }

  @RequestMapping(value = "/api/auth/article/{id}", method = RequestMethod.DELETE)
  public ResponseDto<?> deleteArticle(@PathVariable Long id,
      HttpServletRequest request) {
    return articleService.deleteArticle(id, request);
  }

}
