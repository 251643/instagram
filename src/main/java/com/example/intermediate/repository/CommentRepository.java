package com.example.intermediate.repository;

import com.example.intermediate.model.Comment;
import com.example.intermediate.model.Article;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByArticle(Article article);
}
