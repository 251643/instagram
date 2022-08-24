package com.example.intermediate.controller;

import com.example.intermediate.dto.ResponseDto;
import com.example.intermediate.dto.comment.CommentRequestDto;
import com.example.intermediate.dto.comment.CommentResponseDto;
import com.example.intermediate.service.CommentService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommentController {

  private final CommentService commentService;

  @RequestMapping(value = "/api/auth/comment/{id}", method = RequestMethod.POST)
  public CommentResponseDto createComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto,
                                           HttpServletRequest request) {
    return commentService.createComment(id, requestDto, request);
  }

  @RequestMapping(value = "/api/auth/comment/{id}", method = RequestMethod.GET)
  public List<CommentResponseDto> getAllComments(@PathVariable Long id) {
    return commentService.getAllCommentsByPost(id);
  }
//
//  @RequestMapping(value = "/api/auth/comment/{id}", method = RequestMethod.PUT)
//  public ResponseDto<?> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto,
//      HttpServletRequest request) {
//    return commentService.updateComment(id, requestDto, request);
//  }

  @RequestMapping(value = "/api/auth/comment/{id}", method = RequestMethod.DELETE)
  public boolean deleteComment(@PathVariable Long id,
      HttpServletRequest request) {
    return commentService.deleteComment(id, request);
  }
}
