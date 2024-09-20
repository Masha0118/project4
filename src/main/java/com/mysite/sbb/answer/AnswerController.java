package com.mysite.sbb.answer;

import java.security.Principal;

import com.mysite.sbb.myhistory.History;
import com.mysite.sbb.myhistory.HistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

	private final HistoryService historyService;
	private final AnswerService answerService;
	private final UserService userService;
	@Value("${server.servlet.context-path:}") private String contextPath;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm,
			BindingResult bindingResult, Principal principal) {
		History history = this.historyService.getQuestion(id);
		SiteUser siteUser = this.userService.getUser(principal.getName());
		if (bindingResult.hasErrors()) {
			model.addAttribute("history", history);
			model.addAttribute("principal",principal);
			return "cscenterPage/history_detail";
		}
		Answer answer = this.answerService.create(history, answerForm.getContent(), siteUser);
		model.addAttribute("principal",principal);
		return String.format("redirect:/cscenter/myhistory/detail/%d", id);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal,Model model) {
		Answer answer = this.answerService.getAnswer(id);
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		answerForm.setContent(answer.getContent());
		String exceptP = answerService.exceptP(answer.getContent()); // <p> </p> 제거
		answerForm.setContent(exceptP);

		model.addAttribute("principal",principal);
		return "cscenterPage/answer_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
			@PathVariable("id") Integer id, Principal principal,Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("principal",principal);
			return "cscenterPage/answer_form";
		}
		Answer answer = this.answerService.getAnswer(id);
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		this.answerService.modify(answer, answerForm.getContent());
		model.addAttribute("principal",principal);
		return String.format("redirect:/cscenter/myhistory/detail/%s", answer.getHistory().getId(), answer.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String answerDelete(Principal principal, @PathVariable("id") Integer id,Model model) {
		Answer answer = this.answerService.getAnswer(id);
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}
		this.answerService.delete(answer);
		model.addAttribute("principal",principal);
//		System.out.println("contextPath : " + contextPath);
		model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);
		return String.format("redirect:/cscenter/myhistory/detail/%s", answer.getHistory().getId(), answer.getId());
	}


}
