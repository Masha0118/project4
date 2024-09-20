package com.mysite.sbb.communication;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void saveMessage(ChatMessageDto message, ChatRoom chatRoom) {
        ChatMessageEntity messageEntity = new ChatMessageEntity();
        messageEntity.setSender(message.getSender());
        messageEntity.setRecipient(message.getRecipient());
        messageEntity.setContent(message.getContent());
        messageEntity.setChatRoom(chatRoom);
        messageEntity.setTimestamp(LocalDateTime.now());

        chatMessageRepository.save(messageEntity);
    }

    public List<ChatMessageEntity> getMessagesForRecipient(String recipient) {
        return chatMessageRepository.findByRecipient(recipient);
    }

    public List<ChatMessageEntity> getMessagesBetweenUsers(String sender, String recipient) {
        return chatMessageRepository.findBySenderAndRecipient(sender, recipient);
    }

    public List<ChatMessageEntity> getMessagesByChatRoom(ChatRoom chatRoom) {
        return chatMessageRepository.findByChatRoom(chatRoom);
    }

}
