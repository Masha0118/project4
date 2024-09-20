package com.mysite.sbb.admin.usermanagement;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysite.sbb.mycategory.MyCategoryService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import com.mysite.sbb.user.UserService;

import groovyjarjarantlr4.v4.codegen.model.ModelElement;

@RequestMapping("admin/usermanagement")
@Controller
public class UserManagementController {

	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	MyCategoryService myCategoryService;

    @Value("${server.servlet.context-path:}")
    private String contextPath;
	
	@GetMapping("/inquiry")
	public String usermanagementInquiry(Model model) {
		List<SiteUser> users = userService.getAll();
		Long userCount = userService.getCount();		
		
		Map<LocalDate	, Long> signupStats = userService.getUserSignupStats();

		model.addAttribute("users",users);
		model.addAttribute("userCount",userCount);
//		model.addAttribute("signupStats",signupStats);
//		System.out.println(users);
//    	System.out.println("contextPath : " + contextPath);
    	model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);


		return "adminPage/membership_management";
	}

	@GetMapping("/inquiry/data")
	@ResponseBody

	public Map<String, Long> getUserSignupStatsByPeriod(@RequestParam(value="period") String period) {

		return userService.getSignupStatsByPeriod(period);
//=======
//	public ResponseEntity<Map<String, Long>> getUserSignupStatsByPeriod(@RequestParam("period") String period) {
//		try {
//			System.out.println("Requested period: " + period); // 디버깅 로그 추가
//			Map<String, Long> stats = userService.getSignupStatsByPeriod(period);
//			System.out.println("Stats: " + stats); // 디버깅 로그 추가
//			return new ResponseEntity<>(stats, HttpStatus.OK);
//		} catch (Exception e) {
//			e.printStackTrace(); // 서버 콘솔에 예외 스택 트레이스를 출력합니다.
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//>>>>>>> origin/LDH
	}
	
	@GetMapping("/")
	public String usermanagement(Model model) {
		
		List<SiteUser> users = userService.getAll();
		Long userCount = userService.getCount();		
		model.addAttribute("users",users);
		model.addAttribute("userCount",userCount);
//		System.out.println(users);
//    	System.out.println("contextPath : " + contextPath);
    	model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);

		
		return "adminPage/membership_management_2"; 	
	}
	@GetMapping("/changeRole/{UserId}")
	public ResponseEntity<SiteUser> changeRole(@PathVariable(value="UserId") Long UserId) {
	    SiteUser user = userService.getUserById(UserId);
	    
	    if (user == null) {
	        // 사용자가 존재하지 않는 경우 404 응답 반환
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }

	    // 역할 전환 로직
	    user.setRole(user.getRole().equals("ADMIN") ? "USER" : "ADMIN");

	    // 변경된 사용자 저장
	    user = userService.save(user);
	    
	    // 성공적으로 변경된 사용자 반환
	    return ResponseEntity.ok(user);
	}
	
	
	@GetMapping("/delete/{UserId}")
    public String deleteAccount(@PathVariable(value="UserId") Long userId,Model model) {

            userService.deleteUserById(userId);
            List<SiteUser> users = userService.getAll();
            Long userCount = userService.getCount();

            Map<LocalDate    , Long> signupStats = userService.getUserSignupStats();

            model.addAttribute("users",users);
            model.addAttribute("userCount",userCount);
//            System.out.println(users.toString());
//            System.out.println("contextPath : " + contextPath);
            model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);

            return "adminPage/membership_management_2";

    }
}
