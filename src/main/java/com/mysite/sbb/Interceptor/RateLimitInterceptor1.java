//package com.mysite.sbb.Interceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import java.util.Map;
//import java.util.concurrent.*;
//
//@Component
//public class RateLimitInterceptor1 implements HandlerInterceptor {
//
//    private final Map<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();
//    private final long TIME_WINDOW = TimeUnit.MINUTES.toMillis(1);
//    private final int MAX_REQUEST = 30;
//
//    private static class RequestInfo {
//        int count;
//        long lastRequestTime;
//
//        RequestInfo(int count, long lastRequestTime) {
//            this.count = count;
//            this.lastRequestTime = lastRequestTime;
//        }
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String clientIP = request.getRemoteAddr();
//        long currentTime = System.currentTimeMillis();
//
//        requestCounts.compute(clientIP, (key, requestInfo) -> {
//            if (requestInfo == null || currentTime - requestInfo.lastRequestTime > TIME_WINDOW) {
//                // 새로운 요청이거나, 시간이 지났으면 카운트를 초기화
//                return new RequestInfo(1, currentTime);
//            } else {
//                // 시간 안에 들어온 요청이라면 카운트를 증가
//                requestInfo.count++;
//                return requestInfo;
//            }
//        });
//
//        RequestInfo clientRequestInfo = requestCounts.get(clientIP);
//        if (clientRequestInfo.count > MAX_REQUEST) {
//            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
//            response.getWriter().write("요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");
//            return false;
//        }
//
//        return true;
//    }
//}
//
//
