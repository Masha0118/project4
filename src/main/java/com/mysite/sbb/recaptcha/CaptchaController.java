package com.mysite.sbb.recaptcha;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/verify-captcha")
public class CaptchaController {

    @Autowired
    private RecaptchaService recaptchaService;

    @PostMapping
    public ResponseEntity<Void> verifyCaptcha(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        String token = requestBody.get("token");
        String clientIP = request.getRemoteAddr();

        if (recaptchaService.verifyCaptchaToken(token, clientIP)) {
            return ResponseEntity.ok().build(); // 검증 성공
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 검증 실패
    }
}

