package com.api.localportfogram.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;



@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorizeUser {
    @NotBlank(message = "이메일이 입력되지 않았습니다.")
    @Email(message = "올바른 이메일을 입력해주세요")
    private String email;
    @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
    private String password;

    @Builder
    public AuthorizeUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
