package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController {
//CustomErrorController
    @GetMapping("/custom-error")
    public ModelAndView handleCustomError(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
        mav.addObject("error", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        return mav;
    }
}