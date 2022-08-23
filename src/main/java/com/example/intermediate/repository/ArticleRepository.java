package com.example.intermediate.repository;

import com.example.intermediate.model.Article;
import java.util.List;

import com.example.intermediate.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
  List<Article> findAllByOrderByModifiedAtDesc();

  List<Article> findAllByMemberOrderByModifiedAtDesc(Member member);
  List<Article> findAllByMember(Member member);

}
