package com.mysite.sbb.Interceptor;

import com.mysite.sbb.recaptcha.RecaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RecaptchaService recaptchaService;

    private final Map<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Boolean> captchaVerifiedClients = new ConcurrentHashMap<>(); // 캡차 검증 상태 캐시
    private final long TIME_WINDOW = TimeUnit.MINUTES.toMillis(1);
    private final int MAX_REQUEST = 40;

    private static class RequestInfo {
        int count;
        long lastRequestTime;

        RequestInfo(int count, long lastRequestTime) {
            this.count = count;
            this.lastRequestTime = lastRequestTime;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIP = request.getRemoteAddr();
        String requestPath = request.getRequestURI();
        long currentTime = System.currentTimeMillis();

        // 이미 캡차를 통과한 경우 요청 허용
        if (Boolean.TRUE.equals(captchaVerifiedClients.get(clientIP))) {
            return true;
        }

        // 경로별 요청 카운트 관리
        String key = clientIP + ":" + requestPath;
        requestCounts.compute(key, (k, requestInfo) -> {
            if (requestInfo == null || currentTime - requestInfo.lastRequestTime > TIME_WINDOW) {
                return new RequestInfo(1, currentTime);
            } else {
                requestInfo.count++;
                return requestInfo;
            }
        });

        RequestInfo clientRequestInfo = requestCounts.get(key);
        if (clientRequestInfo.count > MAX_REQUEST) {
            // reCAPTCHA 검증
            String token = request.getParameter("g-recaptcha-response");
            if (token == null || token.isEmpty() || !recaptchaService.verifyCaptchaToken(token, clientIP)) {
                // 검증 실패
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("요청이 너무 많습니다. 리캡챠를 통과하지 못했습니다.");
                return false;
            }
            // 검증 성공 - 상태를 캐시에 저장
            captchaVerifiedClients.put(clientIP, true);
            scheduleCaptchaClear(clientIP); // 일정 시간 후 검증 상태 초기화
        }

        return true;
    }


//    일정 시간 후 클라이언트의 캡차 검증 상태를 초기화

    private void scheduleCaptchaClear(String clientIP) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            captchaVerifiedClients.remove(clientIP);
        }, 1, TimeUnit.HOURS); // 1시간 동안 면제 처리
    }


//     클라이언트가 캡차를 이미 통과했는지 확인
    public boolean isCaptchaVerified(String clientIP) {
        return Boolean.TRUE.equals(captchaVerifiedClients.get(clientIP));
    }
}
