package com.example.intermediate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "image")
@Entity
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //GenerationType.IDENTITY : ID값이 서로 영향없이 자기만의 테이블 기준으로 올라간다.
    private Long id;

    @Column
    private String urlPath;

    @Column
    private String imgUrl;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "articleId", nullable = false)
    private Article article;
}
