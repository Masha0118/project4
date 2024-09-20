package com.mysite.sbb.File;

import com.mysite.sbb.myhistory.History;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FileAttachments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fileName;

    private String filePath;

    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "history_id", nullable = true)
    private History history;
}