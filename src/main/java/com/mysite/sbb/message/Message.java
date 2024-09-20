package com.mysite.sbb.message;

import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private SiteUser sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private SiteUser receiver;

    private String title;

    @Column(length = 3000)
    private String content;

    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

}
