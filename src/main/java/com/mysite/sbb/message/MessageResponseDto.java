package com.mysite.sbb.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponseDto {
    private Long id;
    private String title;
    private String content;
    private String senderUsername; // 메시지 발신자
    private String receiverUsername; // 메시지 수신자
    private String sentAt; // 메시지 발신 시간
}
