package com.example.intermediate.repository;


import com.example.intermediate.model.Article;
import com.example.intermediate.model.Heart;
import com.example.intermediate.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {
  Optional<Heart> findByMemberAndArticle(Member member, Article article);
  List<Heart> findAllByArticle(Article article);
  List<Heart> findAllByMember(Member member);
  boolean existsByMemberAndArticle(Member member, Article article);

  Long countByArticle(Article article);
}
