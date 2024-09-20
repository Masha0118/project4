package com.mysite.sbb.communication;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
public class ChatMessageDto {

    private String sender;
    private String recipient;
    private String content;
    private Long chatRoomId;
    private LocalDateTime timestamp;

    public ChatMessageDto(String sender, String recipient, String content, LocalDateTime timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
    }
}
