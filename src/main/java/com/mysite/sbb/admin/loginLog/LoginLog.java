package com.mysite.sbb.admin.loginLog;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_logs")
@Getter
@Setter
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String status;
    private String message;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
}
