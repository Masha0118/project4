package com.mysite.sbb.notice;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/cscenter")
@Controller
public class NoticeController {


    private final NoticeService noticeService;
    private final UserService userService;

    @GetMapping("/noticelist")
    public String listNotices(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "kw", defaultValue = "") String kw) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Notice> noticePage = noticeService.getNotices(pageable, kw);
        model.addAttribute("noticeList", noticePage.getContent());
        model.addAttribute("paging", noticePage);
        model.addAttribute("kw", kw);
        return "cscenterPage/notice_list";
    }
    
    
    
    @GetMapping("/noticelist/{id}")
    public String viewNotice(@PathVariable(value="id") Integer id, Model model,Principal principal) {
    	if(principal != null) {
    		SiteUser user = userService.getUser(principal.getName());
	    	model.addAttribute("principal",principal);
	    	model.addAttribute("username",user.getId());
    	}
    	
    	
        Notice notice = noticeService.getNoticeById(id);
        model.addAttribute("notice", notice);
        return "cscenterPage/notice_detail"; // 공지사항 상세보기 페이지
    }





    @PreAuthorize("isAuthenticated()")
    @GetMapping("/noticelist/admin/notice/create")
    public String noticeCreate(Model model,NoticeForm noticeForm,Principal principal) {
    	if(principal != null) {
    		SiteUser user = userService.getUser(principal.getName());
	    	model.addAttribute("principal",principal);
	    	model.addAttribute("username",user.getId());
    	}
	        return "cscenterPage/notice_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/noticelist/admin/notice/create")
    public String noticeCreate(@Valid NoticeForm noticeForm, BindingResult bindingResult,Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "cscenterPage/notice_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.noticeService.create(noticeForm.getSubject(), noticeForm.getContent(), siteUser);
        model.addAttribute("principal",principal);
        model.addAttribute("username",siteUser.getId());
        return "redirect:/cscenter/noticelist";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/noticelist/admin/notice/edit/{id}")
    public String editNotice(@PathVariable(value="id") Integer id, Model model, Principal principal) {
        Notice notice = noticeService.findById(id);
        NoticeForm noticeForm = new NoticeForm();
        noticeForm.setSubject(notice.getSubject());
        noticeForm.setContent(notice.getContent());
        model.addAttribute("noticeForm", noticeForm);
        model.addAttribute("principal",principal);
        return "cscenterPage/notice_modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/noticelist/admin/notice/edit/{id}")
    public String updateNotice(@PathVariable(value="id") Integer id, @Valid NoticeForm noticeForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("noticeForm", noticeForm);
            return "cscenterPage/notice_modify";
        }

        // 공지사항을 찾아서 업데이트
        Notice notice = noticeService.findById(id);
        noticeService.updateNotice(notice, noticeForm.getContent(), noticeForm.getSubject());

        // 수정이 완료된 후 공지사항 상세 페이지로 리다이렉션
        return "redirect:/cscenter/noticelist/" + id;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/noticelist/admin/notice/delete/{id}")
    public String deleteNotice(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            noticeService.deleteNotice(id);
            redirectAttributes.addFlashAttribute("message", "공지사항이 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/cscenter/noticelist";
    }

}