package com.mysite.sbb.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@GetMapping("/login")
	public String adminLogin() {
		return "adminPage/login_form_ad";
	}
	
	@GetMapping("/home")
	public String home() {
		return"adminPage/membership_management";
	}
	@GetMapping("/graph")
	public String graph() {
		return"adminPage/graph_ad";
	}
	@GetMapping()
	public String root() {
		return"adminPage/home_ad";
	}
	
}
