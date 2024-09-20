package com.mysite.sbb.friend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class FriendRequestDto {
    private Long id;
    private String friendUsername;
    private String friendMessage;
    private LocalDateTime createdDate;
}
