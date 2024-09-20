package com.mysite.sbb.myhistory;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryForm {
    @NotEmpty(message = "제목은 필수 항목입니다.")
    @Size(max = 200)
    private String subject;

    @NotEmpty(message = "내용은 필수 항목입니다.")
    private String content;

    private boolean secret; // 비번 여부

    private String password; // 비번 필드 추가
}
