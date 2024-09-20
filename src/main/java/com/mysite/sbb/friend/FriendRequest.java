package com.mysite.sbb.friend;

import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"sender_id", "recipient_id"})})
@RequiredArgsConstructor
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private SiteUser sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private SiteUser recipient;

    @Column(nullable = false)
    private String message;

    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

}
