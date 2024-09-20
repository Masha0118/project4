package com.mysite.sbb.navbar;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import com.mysite.sbb.admin.logs.KeywordStatisticsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class NavbarController {

    private static final Logger logger = LoggerFactory.getLogger(NavbarController.class);

    @Value("${file.profile-upload-dir}")
    private String uploadDir;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private final UserService userService;
    private final KeywordStatisticsService keywordStatisticsService;

    @ModelAttribute
    public void addUserAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        if (currentUsername != null) {
            SiteUser user = userService.getUserByUsername(currentUsername);

            if (user != null) {
                String profileImageFilename = user.getProfileImageFilename();

                if (profileImageFilename == null || profileImageFilename.isEmpty()) {
                    // 기본 프로필 이미지 사용
                    model.addAttribute("profileImageFilename", contextPath + "/images/default_profile.jpeg");
                } else {
                    // 사용자 지정 프로필 이미지 사용
                    model.addAttribute("profileImageFilename", contextPath + "/wishlist/profileImage/" + profileImageFilename);
                }

                model.addAttribute("username", currentUsername);
                model.addAttribute("principalName", user.getUsername());
            }
        }

        // 인기 검색어 추가
        List<String> popularKeywords = keywordStatisticsService.getTopKeywords(); // 상위 10개 검색어를 가져옴
        model.addAttribute("popularKeywords", popularKeywords);
        model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);
    }


    @GetMapping("/wishlist/profileImage/{filename}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable("filename") String filename) {
        logger.info("Received request for profile image file: {}", filename);

        try {
            // 서버의 C:/file/profileImage/ 경로에서 파일을 찾음
            Path file = Paths.get(uploadDir).resolve(filename);
            logger.info("Attempting to load profile image from path: {}", file);

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                logger.info("Profile image found and is readable: {}", file);
                System.out.println("resource = " + resource);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFileName().toString() + "\"")
                        .body(resource);
            } else {
                // 파일이 없을 경우 404 반환
                logger.error("Profile image not found or not readable: {}", file);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving profile image file: {}", filename, e);
            return ResponseEntity.notFound().build();
        }
    }
}
