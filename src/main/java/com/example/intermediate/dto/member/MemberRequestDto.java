package com.example.intermediate.dto.member;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {


  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Size(min = 4, max = 12)
  @Pattern(regexp = "[a-zA-Z\\d]*${3,12}")
  private String nickname;

  @NotBlank
  @Size(min = 4, max = 32)
  @Pattern(regexp = "^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[$`~!@$!%*#^?&\\\\(\\\\)\\-_=+]).{3,16}$")
  private String password;
}
