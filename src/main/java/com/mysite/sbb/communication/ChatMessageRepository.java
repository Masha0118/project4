package com.mysite.sbb.communication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {


        List<ChatMessageEntity> findByRecipient(String recipient);
        List<ChatMessageEntity> findBySenderAndRecipient(String sender, String recipient);

        List<ChatMessageEntity> findByChatRoom(ChatRoom chatRoom);

}
