package com.mysite.sbb.message;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;

    public void sendMessage(SiteUser sender, MessageRequestDto messageRequestDto) {
        SiteUser receiver = userService.getUserByUsername(messageRequestDto.getRecipient());

        if (receiver == null) {
            throw new IllegalArgumentException("수신자를 찾을 수 없습니다.");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTitle(messageRequestDto.getTitle());
        message.setContent(messageRequestDto.getContent());

        messageRepository.save(message);
    }

    // 받은 메시지 조회
    public List<MessageResponseDto> getReceivedMessages(SiteUser receiver) {
        return messageRepository.findByReceiver(receiver)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 보낸 메시지 조회
    public List<MessageResponseDto> getSentMessages(SiteUser sender) {
        return messageRepository.findBySender(sender)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 공통 DTO 변환 메서드
    private MessageResponseDto convertToDto(Message message) {
        MessageResponseDto dto = new MessageResponseDto();
        dto.setId(message.getId());
        dto.setTitle(message.getTitle());
        dto.setContent(message.getContent());
        dto.setSenderUsername(message.getSender().getUsername());
        dto.setReceiverUsername(message.getReceiver().getUsername());
//        dto.setSentAt(message.getCreatedAt().toString());  // 필요시 추가
        return dto;
    }
}
