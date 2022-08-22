package com.example.intermediate.dto.article;

import java.util.List;

import com.example.intermediate.dto.comment.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponseDto {
  private Long id;
  private String content;
  private String nickname;
  private boolean isLike;
  private List<CommentResponseDto> commentResponseDtoList;
  private List<ImageResponseDto> imageResponseDtoList;
  private String timeMsg;
  private String heartCnt;
  private String commentCnt;
}
