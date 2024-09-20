package com.mysite.sbb.message;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageController {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/mypage/messages")
    public String getMessages(Model model, @RequestParam("username") String username) {
        // 데이터베이스에서 사용자를 확인합니다.
        SiteUser receiver = userService.getUserByUsername(username);

        if (receiver == null) {
            // 사용자가 존재하지 않는 경우 처리 로직
            model.addAttribute("error", "해당 사용자를 찾을 수 없습니다.");
            return "error"; // 오류 페이지로 리다이렉트하거나 다른 처리
        }

        List<MessageResponseDto> receivedMessages = messageService.getReceivedMessages(receiver);
        List<MessageResponseDto> sentMessages = messageService.getSentMessages(receiver);

        model.addAttribute("receivedMessages", receivedMessages);
        model.addAttribute("sentMessages", sentMessages);

        return "myPage/myPage";
    }

    @GetMapping("/messages/receivedd")
    @ResponseBody
    public ResponseEntity<List<MessageResponseDto>> getReceivedMessages(@RequestParam("username") String username) {
        SiteUser receiver = userService.getUserByUsername(username);
        if (receiver == null) {
            return ResponseEntity.status(404).body(null);
        }

        List<MessageResponseDto> receivedMessages = messageService.getReceivedMessages(receiver);
        return ResponseEntity.ok(receivedMessages);
    }

    @ResponseBody
    @PostMapping("/messages/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageRequestDto messageRequestDto) {

        // 현재 로그인한 사용자 정보를 발신자로 사용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String senderUsername = null;

        if (authentication.getPrincipal() instanceof UserDetails) {
            senderUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        }

//        System.out.println("Sender username: " + senderUsername);

        SiteUser sender = userService.getUserByUsername(senderUsername);
        if (sender == null) {
            return ResponseEntity.status(404).body("발신자를 찾을 수 없습니다.");
        }

//        System.out.println("Recipient username: " + messageRequestDto.getRecipient());

        // 수신자 설정
        SiteUser receiver = userService.getUserByUsername(messageRequestDto.getRecipient());
//        System.out.println("Receiver found: " + receiver);

        if (receiver == null) {
            return ResponseEntity.status(404).body("수신자를 찾을 수 없습니다.");
        }

        // 메시지 전송

        messageService.sendMessage(sender, messageRequestDto);
//        System.out.println("Message sent to: " + receiver.getUsername());

        return ResponseEntity.ok("메시지가 성공적으로 전송되었습니다.");
    }
}
