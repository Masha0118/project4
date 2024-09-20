package com.mysite.sbb.myhistory;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    @SuppressWarnings("unused")
    private Specification<History> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<History> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true); // 중복을 제거
                Join<History, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<History, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"), // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"), // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"), // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%")); // 답변 작성자
            }
        };
    }

    public Page<History> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.historyRepository.findAllByKeyword(kw, pageable);
    }

    // 비밀번호가 없는 경우(비밀번호 없이 게시글 조회할 수 있도록 getQuestion메서드 오버로드)
    public History getQuestion(Integer id) {
        return getQuestion(id, null);
    }

    // 비밀글일 경우 비밀번호 확인 기능 추가
    public History getQuestion(Integer id, String password) {
        Optional<History> history = this.historyRepository.findById(id);

        if (history.isPresent()) {
            History history1 = history.get();

            // 관리자인 경우 비밀번호 확인 생략
            if (!isAdmin()) {
                // 비번 확인 조건문
                if (history1.isSecret()) {
					/* throw new IllegalArgumentException("비밀번호가 일치하지 않습니다."); */
                }
            }

            history1.setView(history1.getView() + 1);
            this.historyRepository.save(history1);
            return history1;

        } else {
            throw new DataNotFoundException("history not found");
        }
    }
    
    public History getQuestion1(Integer id, String password) {
        Optional<History> history = this.historyRepository.findById(id);

        if (history.isPresent()) {
            History history1 = history.get();

            // 관리자인 경우 비밀번호 확인 생략
            if (!isAdmin()) {
                // 비번 확인 조건문
                if (history1.isSecret() && (password == null || !history1.getPassword().equals(password))) {
                    throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
                }
            }

            history1.setView(history1.getView() + 1);
            this.historyRepository.save(history1);
            return history1;

        } else {
            throw new DataNotFoundException("history not found");
        }
    }

    // 비밀글 생성 시 비밀글 여부와 비밀번호를 설정할 수 있도록 추가
    public History create(String subject, String content, SiteUser user, boolean secret, String password) {
        History h = new History();
        h.setSubject(subject);
        h.setContent(content);
        h.setCreateDate(LocalDateTime.now());
        h.setAuthor(user);
        h.setView(0);
        h.setSecret(secret);
        h.setPassword(secret ? password : null);
        this.historyRepository.save(h);
        return h;
    }

    // 게시글 수정할 때 비밀글 여부와 비밀번호 업데이트 할 수 있도록 추가
    public void modify(History history, String subject, String content, boolean secret, String password) {
        history.setSubject(subject);
        history.setContent(content);
        history.setModifyDate(LocalDateTime.now());
        history.setSecret(secret);
        history.setPassword(secret ? password : null);
        this.historyRepository.save(history);
    }

    public void delete(History history) {
        this.historyRepository.delete(history);
    }


    public void toggleVote(History history, SiteUser siteUser) {
        Set<SiteUser> voters = history.getVoter();

        if (voters.contains(siteUser)) {
            // 이미 추천한 상태에서 다시 클릭하면 추천을 제거
            voters.remove(siteUser);
        } else {
            // 추천하지 않은 상태에서 클릭하면 추천을 추가
            voters.add(siteUser);
        }
        history.setVotecount(voters.size());
        historyRepository.save(history);
    }

    // 비밀번호 확인 메서드
    public boolean checkPassword(History history, String password) {
        return history.getPassword().equals(password);
    }

    public List<History> getTop10History() {
        return historyRepository.findTop10ByOrderByVotecountDesc();
    }

    public void deleteHistoryById(int id) {
        historyRepository.deleteById(id);
    }

    public History getHistoryById(int id) {
        return historyRepository.findById(id).orElseThrow(() -> new NoSuchElementException("History not found"));
    }

    public void setSecret(int id, boolean isSecret) {
        History history = getHistoryById(id);
        history.setSecret(isSecret);

        if (isSecret) {
            // 작성자의 비밀번호를 게시글 비밀번호로 설정
            SiteUser author = history.getAuthor();
            if (author != null) {
                history.setPassword(author.getPassword());
            } else {
                throw new IllegalStateException("작성자를 찾을 수 없습니다.");
            }
        } else {
            history.setPassword(null);
        }

        historyRepository.save(history);
    }

    private boolean isAdmin() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
    public Page<History> findAll(int page, int size) {
        return historyRepository.findAll(PageRequest.of(page, size));
    }
}
