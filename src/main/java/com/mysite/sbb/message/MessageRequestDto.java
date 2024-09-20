package com.mysite.sbb.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDto {


    // 리퀘스트 DTO
    private String recipient;
    private String title;
    private String content;
}
