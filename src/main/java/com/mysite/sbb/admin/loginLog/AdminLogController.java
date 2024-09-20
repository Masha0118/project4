package com.mysite.sbb.admin.loginLog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminLogController {

    private LoginLogRepository loginLogRepository;

    @GetMapping("/admin/loginlogs")
    public String viewLonginLogs(Model model) {
        List<LoginLog> logs = loginLogRepository.findAllByOrderByTimestampDesc();
        model.addAttribute("logs", logs);
        return "adminPage/login_logs";
    }
}
