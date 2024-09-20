package com.mysite.sbb.myhistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mysite.sbb.File.FileAttachments;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "history", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL)
    private List<FileAttachments> fileAttachments;

    @ManyToMany
    Set<SiteUser> voter;

    @NotNull
    private Integer votecount = 0;

    @NotNull
    @Column(columnDefinition = "integer default 0")

    private Integer view=0;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean secret; // 게시글이 비밀글인지 아닌지 나타냄 (기본 값 false)

    private String password;    // 비밀번호 저장 필드

    private boolean closed;
}
