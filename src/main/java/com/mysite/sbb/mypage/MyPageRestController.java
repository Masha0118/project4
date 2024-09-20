package com.mysite.sbb.mypage;


import com.mysite.sbb.message.Message;
import com.mysite.sbb.message.MessageResponseDto;
import com.mysite.sbb.message.MessageService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MyPageRestController {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private final MessageService messageService;
    private final UserService userService;

    // 받은 쪽지
    @GetMapping("/messages/received")
    public ResponseEntity<List<MessageResponseDto>> getReceivedMessage(@RequestParam("username") String userId, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;

        if (authentication.getPrincipal() instanceof UserDetails) {
            currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        SiteUser user = userService.getUserById(Long.parseLong(userId));  // userId를 사용하여 사용자 조회

        if (user == null || !user.getUsername().equals(currentUsername)) {
            return ResponseEntity.status(403).build();
        }

        List<MessageResponseDto> receivedMessages = messageService.getReceivedMessages(user);

        return ResponseEntity.ok(receivedMessages);
    }

    // 보낸 쪽지
    @GetMapping("/messages/sent")
    public ResponseEntity<List<MessageResponseDto>> getSentMessages(@RequestParam("username") String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = null;

        if (authentication.getPrincipal() instanceof UserDetails) {
            currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        SiteUser user = userService.getUserById(Long.parseLong(userId));  // userId를 사용하여 사용자 조회

        if (user == null || !user.getUsername().equals(currentUsername)) {
            return ResponseEntity.status(403).build();
        }

        List<MessageResponseDto> sentMessages = messageService.getSentMessages(user);

        return ResponseEntity.ok(sentMessages);
    }




}
