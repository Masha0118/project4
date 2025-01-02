package com.mysite.sbb.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @MessageMapping("/chat.send")
    public ChatMessageDto sendMessage(ChatMessageDto message, Principal principal) {
        String currentUsername = principal.getName();
        message.setSender(currentUsername);

        String sanitizedContent = sanitizeMessageContent(message.getContent());

        // 메시지 길이 제한 (예: 500자 이하)
        if (sanitizedContent.length() > 500) {
            // 메시지가 너무 길면 처리
            sanitizedContent = sanitizedContent.substring(0, 500);
        }

        // 채팅방 이름을 알파벳 순서로 정렬하여 생성
        String chatRoomName = createChatRoomName(currentUsername, message.getRecipient());
        ChatRoom chatRoom = chatRoomService.findOrCreateChatRoom(chatRoomName);

        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setSender(message.getSender());
        chatMessageEntity.setRecipient(message.getRecipient());
        chatMessageEntity.setContent(message.getContent());
        chatMessageEntity.setTimestamp(LocalDateTime.now()); // 타임스탬프 설정
        chatMessageEntity.setChatRoom(chatRoom);

        // 메시지를 해당 채팅방과 연관지어 저장
        chatMessageService.saveMessage(message, chatRoom);

        ChatMessageDto responseMessage = new ChatMessageDto(
                chatMessageEntity.getSender(),
                chatMessageEntity.getRecipient(),
                chatMessageEntity.getContent(),
                chatMessageEntity.getTimestamp()
        );

        // 수신자에게 메시지 보내기
        messagingTemplate.convertAndSendToUser(
                message.getRecipient(),
                "/queue/messages",
                message
        );
        return message;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/chat")
    public String chatPage(@RequestParam("recipientId") Long recipientId, Model model, Principal principal) {
        String currentUsername = principal.getName();

        // recipientId로 username 조회
        Optional<String> recipientUsernameOpt = userService.getUsernameByUserId(recipientId);

        if (recipientUsernameOpt.isPresent()) {
            String recipientUsername = recipientUsernameOpt.get();
            model.addAttribute("recipientId", recipientId);
            model.addAttribute("recipientUsername", recipientUsername);
            model.addAttribute("currentUserId", currentUsername);
            model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);


            // 채팅방 이름을 알파벳 순서로 정렬하여 생성
            String chatRoomName = createChatRoomName(currentUsername, recipientUsername);

            // 채팅방이 이미 존재하는지 확인하고 없으면 새로 생성
            ChatRoom chatRoom = chatRoomService.findOrCreateChatRoom(chatRoomName);

            // 채팅방의 이전 메시지를 불러와서 모델에 추가
            List<ChatMessageEntity> chatHistory = chatMessageService.getMessagesByChatRoom(chatRoom);

            // JSON 변환
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Java 8 시간 모듈 등록
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 타임스탬프로 기록하지 않도록 설정
            try {
                String chatHistoryJson = objectMapper.writeValueAsString(chatHistory);
                model.addAttribute("chatHistoryJson", chatHistoryJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // 예외 처리
            }

            return "chat";
        } else {
            return "error"; // 404 페이지로 리디렉션
        }
    }

    private String createChatRoomName(String user1, String user2) {
        // 알파벳 순으로 정렬하여 채팅방 이름 생성
        if (user1.compareTo(user2) < 0) {
            return user1 + "_" + user2;
        } else {
            return user2 + "_" + user1;
        }
    }

    // 메시지 내용 검증 및 이스케이핑 함수
    private String sanitizeMessageContent(String content) {
        if (content == null) {
            return "";
        }
        // Spring의 HtmlUtils를 사용하여 HTML 이스케이핑 처리
        return HtmlUtils.htmlEscape(content);
    }
}
