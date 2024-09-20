package com.mysite.sbb.admin.CsCenterManagement;

import com.mysite.sbb.myhistory.History;
import com.mysite.sbb.myhistory.HistoryService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/cscenter")
public class CsCenterManagementController {
	
	@Value("${server.servlet.context-path:}")
	private String contextPath;

    private final CsCenterManagementService csCenterManagementService;
    private final HistoryService historyService;

    @GetMapping("/historylist")
    public String getAllHistories(Model model, @RequestParam(value="page",defaultValue = "1") Integer page) {
        int pageSize = 10; // 페이지당 항목 수

        // 페이지는 0부터 시작하므로 -1
        Page<History> historyPage = historyService.findAll(page - 1, pageSize);

//        System.out.println("contextPath : " + contextPath);
        model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);

        model.addAttribute("allHistories", historyPage.getContent());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", historyPage.getTotalPages());

        return "adminPage/all_histories"; // all_histories로 변경
    }
    @GetMapping("/helpfulQuestion")
    public String gethelpfulQuestion(Model model) {
//		System.out.println("contextPath : " + contextPath);
		model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);
        List<History> history = historyService.getTop10History();
        model.addAttribute("history", history);

        return "adminPage/helpful_question";
    }
}
