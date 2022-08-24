package com.example.intermediate.dto.heart;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class HeartResponseDto {
    private long articleId;
    private long heartCnt;
    private boolean isLike;
}