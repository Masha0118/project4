package com.mysite.sbb.admin.loginLog;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationHandlers {

    private final LoginLogRepository loginLogRepository;

    public AuthenticationSuccessHandler authenticationSuccessHandler() {

        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

                LoginLog log = new LoginLog();
                log.setUsername(authentication.getName());
                log.setStatus("SUCCESS");
                log.setMessage("User successfully logged in.");
                log.setTimestamp(LocalDateTime.now());
                loginLogRepository.save(log);

                response.sendRedirect("/");
            }
        };
    }


    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

                LoginLog log = new LoginLog();
                log.setUsername(request.getParameter("username"));
                log.setStatus("FAILURE");
                log.setMessage("Login attempt failed: " + exception.getMessage());
                log.setTimestamp(LocalDateTime.now());
                loginLogRepository.save(log);

                response.sendRedirect("/login?error");
            }
        };
    }
}
