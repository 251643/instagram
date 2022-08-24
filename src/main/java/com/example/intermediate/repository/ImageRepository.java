package com.example.intermediate.repository;


import com.example.intermediate.model.Article;
import com.example.intermediate.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByArticle(Article article);
}
