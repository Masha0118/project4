 package com.mysite.sbb.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.function.EntityResponse;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
	
	@Autowired
	TestService testService;
	
	@GetMapping("/insert")
	public String insetForm() {
		return "test/test_input_form";
	}
	
	@PostMapping("/insertProc")
	public String insertProcess(@Valid TestUserDTO userDTO, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {
			return "redirect:/test/insert";
		}else {
		testService.saveUser(userDTO.getName(), userDTO.getAge());
		return "redirect:/test/insert";
		
		}
	}
	
	
	@GetMapping("/error")
	public String getMethodName() {
		return "test_error";
	}
	
	@GetMapping("/insert/new")
	public ResponseEntity<List<TestUser>> getUserlist(){
		
		List<TestUser> testUserList =testService.getAll();
		
		
		return ResponseEntity.ok(testService.getAll());
		
	} 
	@GetMapping("/insert/delete")
	public String allDelete() {
		
		testService.allDelete();
		
		return "redirect:/test/insert";
		
	}
	
	@GetMapping("/insert/delete/{userId}")
	public String indiDelete(@PathVariable("userId") Long id) {
		
		testService.deleteById(id);
		
		return "redirect:/test/insert";
		
	}	
	
	

}
