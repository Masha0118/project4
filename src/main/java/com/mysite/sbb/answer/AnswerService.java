package com.mysite.sbb.answer;

import java.time.LocalDateTime;
import java.util.Optional;

import com.mysite.sbb.myhistory.History;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AnswerService {

	private final AnswerRepository answerRepository;

	public Answer create(History history, String content, SiteUser author) {
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setCreateDate(LocalDateTime.now());
		answer.setHistory(history);
		answer.setAuthor(author);
		this.answerRepository.save(answer);
		return answer;
	}

	public Answer getAnswer(Integer id) {
		Optional<Answer> answer = this.answerRepository.findById(id);
		if (answer.isPresent()) {
			return answer.get();
		} else {
			throw new DataNotFoundException("answer not found");
		}
	}

	public void modify(Answer answer, String content) {
		answer.setContent(content);
		answer.setModifyDate(LocalDateTime.now());
		this.answerRepository.save(answer);
	}

	public void delete(Answer answer) {
		this.answerRepository.delete(answer);
	}

	public void vote(Answer answer, SiteUser siteUser) {
		answer.getVoter().add(siteUser);
		this.answerRepository.save(answer);
	}
	public static String exceptP(String input) {
		return input
				.replaceAll("<p>", "")         // <p> 태그 제거
				.replaceAll("</p>", "");       // </p> 태그 제거
		// 다른 HTML 태그는 제거하지 않음
	}
}
