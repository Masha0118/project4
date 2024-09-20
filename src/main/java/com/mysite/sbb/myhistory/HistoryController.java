package com.mysite.sbb.myhistory;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.sbb.File.FileAttachmentService;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/cscenter")
@RequiredArgsConstructor
@Controller
public class HistoryController {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private final HistoryService historyService;
    private final UserService userService;
    private final FileAttachmentService fileAttachmentService;


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myhistory")
    public String myHistory(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "kw", defaultValue = "") String kw, Principal principal) {

        if (principal != null) {
            SiteUser user = userService.getUser(principal.getName());
            log.info("page:{}, kw:{}", page, kw);
            Page<History> paging = this.historyService.getList(page, kw);
            model.addAttribute("paging", paging);
            model.addAttribute("kw", kw);
            model.addAttribute("principal", principal);
            model.addAttribute("username", user.getId());
        }
        return "cscenterPage/myhistory";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/regcs")
    public String regCs(HistoryForm historyForm, Model model, Principal principal) {
        if (principal != null) {
//            System.out.println("contextPath : " + contextPath);
            model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);
            SiteUser user = userService.getUser(principal.getName());
            model.addAttribute("principal", principal);
            model.addAttribute("username", principal.getName());
            model.addAttribute("username", user.getId());
        }
        return "cscenterPage/regcs_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/regcs")
    public String regCs(@Valid HistoryForm historyForm, BindingResult bindingResult, Model model, Principal principal,
                        @RequestParam(value = "files", required = false) MultipartFile[] files) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("principal", principal);
            return "cscenterPage/regcs_form";
        }

        SiteUser siteUser = this.userService.getUser(principal.getName());
        // 비밀글 여부와 비밀번호를 포함한 폼 데이터 처리 추가
        History history = this.historyService.create(historyForm.getSubject(), historyForm.getContent(), siteUser,
                historyForm.isSecret(), historyForm.getPassword());

        // 파일 저장 처리
        if (files != null && files.length > 0) {
            List<MultipartFile> fileList = Arrays.stream(files)
                    .filter(file -> !file.isEmpty())
                    .collect(Collectors.toList());

            if (!fileList.isEmpty()) {
                try {
                    fileAttachmentService.saveFiles(fileList, history, principal.getName());
                } catch (IOException e) {
                    log.error("File upload error", e);
                    // 사용자에게 에러 메시지 전달 로직 필요
                    return "cscenterPage/regcs_form"; // 파일 업로드 실패 시 폼으로 돌아가기
                }
            }
        }

        model.addAttribute("principal", principal);
        return "redirect:/cscenter/myhistory";
    }

    @GetMapping(value = "/myhistory/detail/{id}")
    public String detail(Model model, @PathVariable(value="id") Integer id, @RequestParam(value = "password", required = false) String password,
                         AnswerForm answerForm, Principal principal) {

        try {
            SiteUser user = userService.getUser(principal.getName());
            History question = this.historyService.getQuestion1(id, password);
            model.addAttribute("question", question);
            model.addAttribute("principal", principal);
            model.addAttribute("username", user.getId());
            return "cscenterPage/regcs_detail";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("historyId", id);
            return "cscenterPage/password_check";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myhistory/modify/{id}")
    public String historyModify(Model model, HistoryForm historyForm, @PathVariable("id") Integer id,
                                @RequestParam(value ="password",required = false) String password, Principal principal) {
        // 비밀글 수정 시 비밀번호 확인 추가
        try {
            History history = this.historyService.getQuestion(id, password);
            if (!history.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            }
            History question = this.historyService.getQuestion(id, password);
            model.addAttribute("question", question);
            historyForm.setSubject(history.getSubject());
            historyForm.setContent(history.getContent());
            historyForm.setSecret(history.isSecret());
            historyForm.setPassword(history.getPassword());
            model.addAttribute("principal", principal);
            return "cscenterPage/regcs_form";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("historyId", id);
            return "cscenterPage/password_check";

        }
    }






    @PreAuthorize("isAuthenticated()")
    @PostMapping("/myhistory/modify/{id}")
    public String historyModify(@Valid HistoryForm historyForm, BindingResult bindingResult, Model model,
                                Principal principal,
                                @PathVariable(value="id") Integer id, @RequestParam(value ="password",required = false) String password) {
        if (bindingResult.hasErrors()) {
            return "cscenterPage/regcs_form";
        }
        // 비밀글 수정 시 비밀번호 확인 추가
        try {
            History history = this.historyService.getQuestion1(id, password);
            if (!history.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            }
            this.historyService.modify(history, historyForm.getSubject(), historyForm.getContent(),
                    historyForm.isSecret(), historyForm.getPassword());
            History question = this.historyService.getQuestion(id, password);
            model.addAttribute("principal", principal);
            model.addAttribute("question", question);
            return String.format("redirect:/cscenter/myhistory/detail/%s", id);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("historyId", id);
            return "cscenterPage/password_check";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myhistory/delete/{id}")
    public String historyDelete(Principal principal, @PathVariable(value="id") Integer id,
                                @RequestParam(value = "password",required = false) String password, Model model) {
        // 비밀글 삭제 시 비밀번호 확인을 위한 코드 추가
        try {
            History history = this.historyService.getQuestion(id, password);
            if (!history.getAuthor().getUsername().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
            }
            this.historyService.delete(history);
            model.addAttribute("principal", principal);
            return "redirect:/cscenter/myhistory";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("historyId", id);
            return "cscenterPage/password_check";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myhistory/vote/{id}")
    public String toggleVote(Principal principal, @PathVariable(value="id") Integer id) {
        History history = this.historyService.getQuestion(id); // 비밀번호 없이 게시글 조회 가능(historyService getQuestion)
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.historyService.toggleVote(history, siteUser);
        return String.format("redirect:/cscenter/myhistory/detail/%s", id);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top10notice")
    public String getTop10History(Principal principal, Model model) {
        SiteUser user = userService.getUser(principal.getName());
        List<History> history = historyService.getTop10History();
        model.addAttribute("history", history);
        model.addAttribute("username", user.getId());
        model.addAttribute("principal", principal);

        return "cscenterPage/top10notice";
    }

    // 관리자 페이지 질문 삭제 API
    @DeleteMapping("/myhistory/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteQuestion(@PathVariable(value="id") int id) {
        try {
            historyService.deleteHistoryById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/myhistory/setsecret/{id}")
    public ResponseEntity<?> setSecret(@PathVariable(value="id") int id, @RequestBody Map<String, Boolean> payload) {
        boolean isSecret = payload.get("secret");

        try {
            historyService.setSecret(id, isSecret);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getHistoryDetail(@PathVariable(value="id") int id, Principal principal) {
        History history = historyService.getHistoryById(id);
        String username = principal.getName();

        if (history.isSecret() && !history.getAuthor().getUsername().equals(username)) {
            return new ResponseEntity<>("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 게시글 상세 내용을 반환합니다. 이 부분은 실제 요구 사항에 맞게 수정하세요.
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    @PostMapping("/checkPassword/{id}")
    public ResponseEntity<?> checkPassword(Model model,@PathVariable(value="id") Integer id, @RequestBody Map<String, String> payload) {
        String password = payload.get("password");

        try {
            History question = this.historyService.getQuestion(id, password);
            model.addAttribute("question", question);
            History history = historyService.getHistoryById(id); // History 객체를 가져옴
            boolean passwordMatches = historyService.checkPassword(history, password);
            if (passwordMatches) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("비밀번호가 일치하지 않습니다.", HttpStatus.FORBIDDEN);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}