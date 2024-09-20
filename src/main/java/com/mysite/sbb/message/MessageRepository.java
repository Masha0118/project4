package com.mysite.sbb.message;

import com.mysite.sbb.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByReceiver(SiteUser receiver);

    List<Message> findBySender(SiteUser sender);

    List<Message> findByReceiverOrderBySentAtDesc(SiteUser receiver);
}
