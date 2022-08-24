package com.example.intermediate.controller;

import com.example.intermediate.dto.heart.HeartResponseDto;
import com.example.intermediate.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HeartController {

    private final HeartService heartService;

    @RequestMapping(value = "/api/auth/heart/{id}", method = RequestMethod.POST)
    public HeartResponseDto createComment(@PathVariable Long id, HttpServletRequest request) {
        return heartService.addHeart(id, request);
    }

}
