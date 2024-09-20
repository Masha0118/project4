package com.mysite.sbb.notice;


import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import com.mysite.sbb.user.SiteUser;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;


@RequiredArgsConstructor
@Service
public class NoticeService {
    private final NoticeRepository notiiceRepository;

    public List<Notice> getList() {
        return this.notiiceRepository.findAll();
    }

    public void create(String subject, String content, SiteUser user) {
        Notice notice = new Notice();
        notice.setSubject(subject);
        notice.setContent(content);
        notice.setCreateDate(LocalDateTime.now());
        this.notiiceRepository.save(notice);
    }

    public List<Notice> getRecentNotice() {
        return notiiceRepository.findTop3ByOrderByIdDesc();
    }

    public Notice findById(Integer id) {
        return notiiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + id));
    }

    public void updateNotice(Notice notice, String content, String subject) {
        notice.setSubject(subject);
        notice.setContent(content);
        notiiceRepository.save(notice);
    }

    public void deleteNotice(Integer id) {
        if (notiiceRepository.existsById(id)) {
            notiiceRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("공지사항이 존재하지 않습니다.");
        }
    }
    

    
    public Page<Notice> getNoticeList(Pageable pageable) {
        return notiiceRepository.findAll(pageable);
    }

//    public Page<Notice> searchNotices(String keyword, Pageable pageable) {
//        if (keyword == null || keyword.trim().isEmpty()) {
//            return notiiceRepository.findAll(pageable);
//        }
//
//        // keyword를 포함하는 제목 또는 내용의 공지사항을 찾습니다.
//        return notiiceRepository.findBySubjectContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
//    }
    public Page<Notice> getNotices(Pageable pageable, String keyword) {
        return notiiceRepository.findBySubjectContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
    }

    public Notice getNoticeById(Integer id) {
        return notiiceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("공지사항이 존재하지 않습니다."));
    }
}

