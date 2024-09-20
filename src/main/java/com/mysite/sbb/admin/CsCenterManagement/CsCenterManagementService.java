package com.mysite.sbb.admin.CsCenterManagement;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.myhistory.History;
import com.mysite.sbb.myhistory.HistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CsCenterManagementService {

    private final CsCenterManagementRepository csCenterManagementRepository;
    private final HistoryRepository historyRepository;
    private final AnswerRepository answerRepository;

    public List<History> getHistoriesWithoutAnswers() {
        return csCenterManagementRepository.findHistoriesWithoutAnswers();
    }

    public List<History> getAllHistory() {
        return csCenterManagementRepository.findAll();
    }

    @Transactional
    public void createAnswer(int questionId, Answer answer) {
        History history = historyRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Invalid question Id:" + questionId));
        answer.setHistory(history);
        answerRepository.save(answer);

        // 질문 상태를 종료로 업데이트
        history.setClosed(true);
        historyRepository.save(history);
    }
}
