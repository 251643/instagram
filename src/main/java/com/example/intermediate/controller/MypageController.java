package com.example.intermediate.controller;


import com.example.intermediate.dto.ResponseDto;
import com.example.intermediate.dto.article.ArticleResponseDto;
import com.example.intermediate.model.UserDetailsImpl;
import com.example.intermediate.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MypageController {

    private final MypageService mypageService;

    @RequestMapping(value = "/api/auth/mypage/myarticle", method = RequestMethod.GET)
    public List<ArticleResponseDto> getMyArticles(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return mypageService.getMyArticles(userDetailsImpl);
    }

    @RequestMapping(value = "/api/auth/mypage/myheart", method = RequestMethod.GET)
    public List<ArticleResponseDto> getMyHeartArticles(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        return mypageService.getMyHeartArticles(userDetailsImpl);
    }

}
