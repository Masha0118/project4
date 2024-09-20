package com.mysite.sbb.communication;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<ChatRoom> findById(Long id) {
        return chatRoomRepository.findById(id);
    }

    public ChatRoom createChatRoom(String chatRoomName) {

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(chatRoomName);
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom findOrCreateChatRoom(String chatRoomName) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByName(chatRoomName);
        return existingRoom.orElseGet(() -> createChatRoom(chatRoomName));
    }
}
