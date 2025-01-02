package com.mysite.sbb.Interceptor;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/check-rate-limit")
public class RateLimitController {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @GetMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Boolean>> checkRateLimit(HttpServletRequest request) {
        String clientIP = request.getRemoteAddr();
        boolean requiresCaptcha = !rateLimitInterceptor.isCaptchaVerified(clientIP);
        return ResponseEntity.ok(Map.of("requiresCaptcha", requiresCaptcha));
    }
}
