package com.mysite.sbb;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
// CustomAuthenticationFailureHandler
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String errorMessage; // 기본 오류 메시지

        if (exception instanceof UsernameNotFoundException) {
            String msg = exception.getMessage();
//            System.out.println("로그인 실패 예외 메시지: " + msg); // 디버깅을 위한 로그
            if ("인증되지 않은 계정입니다.".equals(msg)) {
                errorMessage = "인증되지 않은 계정입니다.";
            } else if ("사용자를 찾을 수 없습니다.".equals(msg)) {
                errorMessage = "사용자를 찾을 수 없습니다.";
            } else {
                errorMessage = "로그인 실패";
            }
        } else if (exception instanceof BadCredentialsException) {
            // 비밀번호 오류 처리
            errorMessage = "사용자ID 또는 비밀번호를 확인해 주세요.";
        } else {
            errorMessage = "로그인 실패";
        }

        // 오류 메시지를 쿼리 파라미터로 전달
        getRedirectStrategy().sendRedirect(request, response, "/user/login?error=true&message=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.name()));
    }
}