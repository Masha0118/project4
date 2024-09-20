package com.mysite.sbb.cscenter;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mysite.sbb.notice.Notice;
import com.mysite.sbb.notice.NoticeService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller

public class CsCenterController {

    private final NoticeService noticeService;
    private final UserService userService;

    @GetMapping("/cscenter")
    public String csCenterHomepage(Model model,Principal principal) {
    	
        List<Notice> recentNotice = noticeService.getRecentNotice();
        model.addAttribute("recentNotice", recentNotice);
		model.addAttribute("principal",principal);
        return "cscenterPage/cscenter";
    }

}
