package com.mysite.sbb;

import com.mysite.sbb.Interceptor.RateLimitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${profile.image.location}")
    private String profileImageLocation;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private final RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "https://wishlink.duckdns.org")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

//		  registry.addResourceHandler("/images/**")
//		 .addResourceLocations("file:C:/file/profileImage") .setCachePeriod(0); // 캐시 비활성화

        // 프로필 이미지 경로 추가


        registry.addResourceHandler( "/wishlist/profileImage/**")
                .addResourceLocations(profileImageLocation)
                .setCachePeriod(0); // 캐시 비활성화


//        registry.addResourceHandler( "/home/ubuntu/sbb/C:/file/profileImage/**")
//                .addResourceLocations(profileImageLocation)
//                .setCachePeriod(0); // 캐시 비활성화
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/**");
    }
}
