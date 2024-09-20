package com.mysite.sbb.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    List<Notice> findBySubjectLike(String subject);

    List<Notice> findTop3ByOrderByIdDesc();

    Page<Notice> findBySubjectContainingIgnoreCaseOrContentContainingIgnoreCase(String subjectKeyword, String contentKeyword, Pageable pageable);

}
