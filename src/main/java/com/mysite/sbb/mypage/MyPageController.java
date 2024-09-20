package com.mysite.sbb.mypage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private static final String DEFAULT_PROFILE_IMAGE = "/images/default-profile.jpeg";
    private static final String UPLOAD_ERROR_MESSAGE = "파일 업로드 중 오류가 발생했습니다.";
    private static final String UPLOAD_SUCCESS_MESSAGE = "프로필 이미지가 성공적으로 업로드되었습니다.";

//    private final String profileImageDirectory = "/home/ubuntu/sbb/C:/file/profileImage";
    private final String profileImageDirectory = "C:/file/profileImage";


    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${file.profile-upload-dir}")
    private String uploadDir;

    private final UserService userService;

    @GetMapping("/{id}")
    public String getMyPage(@PathVariable(value="id") Long id, Model model, Principal principal) {
        SiteUser user = getAuthenticatedUser(principal);
        if (user == null || !user.getId().equals(id)) {
            return "redirect:/home";
        }

        model.addAttribute("userId", id);
        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("currentUsername", user.getUsername());
        model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);

        // 이미지 URL 생성
        String profileImageUrl = user.getProfileImageFilename() != null
                ? "/mypage/" + id + "/profile-image"
                : DEFAULT_PROFILE_IMAGE;
        model.addAttribute("profileImageUrl", profileImageUrl);

        return "mypage/mypage";
    }

    @PostMapping("/{id}/uploadprofile")
    public String uploadProfile(@PathVariable(value="id") Long id, @RequestParam("profileImage") MultipartFile file, RedirectAttributes redirectAttributes) {
        SiteUser user = userService.getUserById(id);
        if (!isUserAuthenticated(user)) {
            return "redirect:/home";
        }

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "파일을 선택해 주세요.");
            return "redirect:/mypage/" + id;
        }

        try {
            saveProfileImage(user, file);
            redirectAttributes.addFlashAttribute("success", UPLOAD_SUCCESS_MESSAGE);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", UPLOAD_ERROR_MESSAGE);
        }

        return "redirect:/mypage/" + id;
    }

    @GetMapping("/{id}/profile-image")
    public ResponseEntity<Resource> getProfileImage(@PathVariable(value="id") Long id) {
        SiteUser user = userService.getUserById(id);
        try {
            Resource resource = loadProfileImage(user.getProfileImageFilename());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            Resource defaultImage = new ClassPathResource(DEFAULT_PROFILE_IMAGE);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + defaultImage.getFilename() + "\"")
                    .body(defaultImage);
        }
    }

    private SiteUser getAuthenticatedUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        return userService.getUser(principal.getName());
    }

    private boolean isUserAuthenticated(SiteUser user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;

        if (authentication.getPrincipal() instanceof UserDetails) {
            currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        return currentUsername != null && currentUsername.equals(user.getUsername());
    }

    private void saveProfileImage(SiteUser user, MultipartFile file) throws IOException {
        // 기존 파일이 존재하면 삭제
        if (user.getProfileImageFilename() != null) {
            Path existingImagePath = Paths.get(uploadDir, user.getProfileImageFilename());
            Files.deleteIfExists(existingImagePath);
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // 업로드 디렉토리가 존재하지 않으면 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path path = uploadPath.resolve(fileName);
        Files.write(path, file.getBytes());

        user.setProfileImageFilename(fileName);
        userService.save(user);
    }

    private Resource loadProfileImage(String filename) throws IOException {
        if (filename != null) {
            Path imagePath = Paths.get(uploadDir, filename);
            Resource resource = new UrlResource(imagePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
        }
        throw new IOException("Profile image not found or not readable");
    }


    @GetMapping("/profileImage/{username}")
    @ResponseBody
    public Resource getProfileImage(@PathVariable String username) throws Exception {
        List<String> extensions = Arrays.asList("png", "jpg", "jpeg"); // 지원하는 확장자
        Path imagePath = null;

        // 유효한 확장자가 있는지 확인하고 파일 경로 찾기
        for (String extension : extensions) {
            Path path = Paths.get(profileImageDirectory, username + "." + extension);
            if (Files.exists(path)) {
                imagePath = path;
                break;
            }
        }

        // 프로필 이미지가 없다면 기본 이미지 반환
        if (imagePath == null) {
            imagePath = Paths.get(profileImageDirectory, "default_profile.jpeg");
        }

        return new UrlResource(imagePath.toUri());
    }
}
