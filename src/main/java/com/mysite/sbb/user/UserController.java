package com.mysite.sbb.user;

import java.util.HashMap;
import java.util.Map;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

	@Value("${server.servlet.context-path:}") private String contextPath;
	
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final EncryptionService encryptionService;

	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm,Principal principal,Model model) {
		if(principal != null) {
			
			
			
//			System.out.println("contextPath : " + contextPath);
			model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);
			SiteUser user = userService.getUser(principal.getName());
			model.addAttribute("principal",principal);
			model.addAttribute("username",user.getId());
//			System.out.println("contextPath : " + contextPath);
			model.addAttribute("contextPath", contextPath == null || contextPath.isEmpty() ? "" : contextPath);
		}

		return "signupPage/signup_form";
	}

	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "signupPage/signup_form";
		}

		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			return "signupPage/signup_form";
		}

		try {
			userService.create(
					userCreateForm.getUsername(),
					userCreateForm.getEmail(),
					userCreateForm.getPassword1(),
					userCreateForm.getName(),
					userCreateForm.getTel(),
					userCreateForm.getSecurityQuestion(),
					userCreateForm.getSecurityAnswer(),
					userCreateForm.getAddress(),
					userCreateForm.getAddressDetail(),
					userCreateForm.getZipcode(),
					userCreateForm.getRrnFront(),
					userCreateForm.getRrnBack()
			);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "signupPage/signup_form";
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signupPage/signup_form";
		}

		return "signupPage/temp";
	}

	@GetMapping("/verify-email")
	public String verifyEmail(@RequestParam("token") String token, Model model) {
		String result = userService.validateVerificationToken(token);
		if (result.equals("valid")) {
			model.addAttribute("message", "이메일 인증 성공");
			return "signupPage/verify_email";
		} else {
			model.addAttribute("message", "유효하지 않은 토큰입니다.");
			return "signupPage/verify_email";
		}
	}

	@GetMapping("/login")
	public String login(Principal principal,Model model) {
		if(principal != null) {
			SiteUser user = userService.getUser(principal.getName());
			model.addAttribute("principal",principal);
			model.addAttribute("username",user.getId());
		}
		
		return "loginPage/login_form";
	}

	@PostMapping("/login-success")
	public String loginSuccess() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		SiteUser user = (SiteUser) authentication.getPrincipal();
		return "redirect:/wishlist/" + user.getId();
	}

	// 모든 사용자 목록을 가져오는 엔드포인트 추가
	@GetMapping("/all")
	@ResponseBody
	public ResponseEntity<List<SiteUser>> getAllUsers() {
		try {
			List<SiteUser> users = userService.getAll();
			return new ResponseEntity<>(users, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/change-password")
	public String showChangePasswordPage() {
		return "change_password";
	}


	@PostMapping("/change-password")
	public String changePassword(@RequestParam("username1") String username,
								 @RequestParam("oldPassword") String oldPassword,
								 @RequestParam("newPassword1") String newPassword1,
								 @RequestParam("newPassword2") String newPassword2,
								 Model model,
								 RedirectAttributes redirectAttributes) {

		// 비밀번호 입력란이 비어 있는지 확인
		if (username == null || username.trim().isEmpty() ||
				oldPassword == null || oldPassword.trim().isEmpty() ||
				newPassword1 == null || newPassword1.trim().isEmpty() ||
				newPassword2 == null || newPassword2.trim().isEmpty()) {
			model.addAttribute("error", "모든 필드를 입력하세요.");
			return "change_password";
		}

		// 새 비밀번호와 확인 비밀번호가 일치하는지 확인
		if (!newPassword1.equals(newPassword2)) {
			model.addAttribute("error", "새 비밀번호가 일치하지 않습니다.");
			return "change_password";
		}

		try {
			// 아이디로 사용자 조회
			SiteUser user = userService.getUser(username);
//			System.out.println(user);
			// 기존 비밀번호가 일치하는지 확인
			if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
				model.addAttribute("error", "기존 비밀번호가 일치하지 않습니다.");
				return "change_password";
			}

			// 기존 비밀번호와 새 비밀번호가 동일한지 확인
			if (passwordEncoder.matches(newPassword1, user.getPassword())) {
				model.addAttribute("error", "기존 비밀번호와 동일한 비밀번호입니다. 다른 비밀번호를 입력하세요.");
				return "change_password";
			}

			// 새 비밀번호로 변경
			userService.changePassword(user, newPassword1);

			// 비밀번호 변경 성공 상태를 리다이렉트 시 전달
			redirectAttributes.addFlashAttribute("passwordChanged", true);
			return "redirect:/mypage/" + user.getId();

		} catch (Exception e) {
			model.addAttribute("error", "비밀번호 변경 중 오류가 발생했습니다. 다시 시도하세요.");
			return "change_password";
		}
	}
	// 회원가입 시 아이디 중복 체크
	@GetMapping("/check-username")
	@ResponseBody
	public ResponseEntity<Boolean> checkUsername(@RequestParam("username") String username) {
		boolean isAvailable = userService.isUsernameAvailable(username);
		return new ResponseEntity<>(isAvailable, HttpStatus.OK);
	}

	// 비밀번호 찾기 페이지로 이동
	@GetMapping("/forgot-password")
	public String showForgotPasswordPage() {
		return "forgot_password";
	}

	// 비밀번호 분실 질문 체크
	@GetMapping("/check-security-question")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> checkSecurityQuestion(@RequestParam("name") String name, @RequestParam("username") String username) {
		Map<String, Object> response = new HashMap<>();
		try {
			SiteUser user = userService.getUserByUsername(username);
			if (user != null && user.getName().equals(name)) {
				response.put("success", true);
				response.put("securityQuestion", user.getSecurityQuestion()); // 보안 질문 반환
			} else {
				response.put("success", false);
			}
		} catch (Exception e) {
			response.put("success", false);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// 비밀번호 분실 답변 체크
	@PostMapping("/verify-security-answer")
	@ResponseBody
	public ResponseEntity<String> verifySecurityAnswer(@RequestParam("username") String username,
													   @RequestParam("securityAnswer") String securityAnswer) {
		try {
			SiteUser user = userService.getUserByUsername(username);
			if (user.getSecurityAnswer().equals(securityAnswer)) {
				return ResponseEntity.ok("success");
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
		}
	}

	// 비밀번호 분실 후 비밀번호 재설정 페이지로 이동
	@GetMapping("/forgot-change-password")
	public String showForgotChangePasswordPage(@RequestParam("username") String username, Model model,Principal principal) {
		if(principal != null) {
			SiteUser user = userService.getUser(principal.getName());
			model.addAttribute("principal",principal);
			model.addAttribute("username",user.getId());
		}
		model.addAttribute("username1", username);
		return "forgot_change_password";
	}

	// 비밀번호 분실 후 비밀번호 재설정
	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam("username1") String username,
								@RequestParam("newPassword1") String newPassword1,
								@RequestParam("newPassword2") String newPassword2,
								Model model) {

		// 비밀번호 입력란이 비어 있는지 확인
		if (newPassword1 == null || newPassword1.trim().isEmpty() ||
				newPassword2 == null || newPassword2.trim().isEmpty()) {
			model.addAttribute("error", "비밀번호를 입력하세요.");
			model.addAttribute("username", username);  // username 값을 다시 전달
			return "forgot_change_password";
		}

		// 새 비밀번호와 확인 비밀번호가 일치하는지 확인
		if (!newPassword1.equals(newPassword2)) {
			model.addAttribute("error", "새 비밀번호가 일치하지 않습니다.");
			model.addAttribute("username", username);  // username 값을 다시 전달
			return "forgot_change_password";
		}

		try {
			SiteUser user = userService.getUserByUsername(username);

			// 기존 비밀번호와 새 비밀번호가 동일한지 확인
			if (passwordEncoder.matches(newPassword1, user.getPassword())) {
				model.addAttribute("error", "기존 비밀번호와 동일한 비밀번호입니다. 다른 비밀번호를 입력하세요.");
				model.addAttribute("username", username);  // username 값을 다시 전달
				return "forgot_change_password";
			}

			// 새 비밀번호로 변경
			userService.resetPassword(username, newPassword1);
			return "redirect:/user/login?passwordChanged=true"; // 비밀번호 재설정 완료 후 로그인 페이지로 리디렉션
		} catch (Exception e) {
			model.addAttribute("error", "비밀번호 재설정 중 오류가 발생했습니다. 다시 시도하세요.");
			model.addAttribute("username", username);  // username 값을 다시 전달
			return "forgot_change_password";
		}
	}

	@GetMapping("/forgot-id")
	public String showForgotIdPage() {
		return "forgot_id";  // `forgot_id.html` 파일로 이동
	}

	@PostMapping("/forgotIdProc")
	public String processForgotId(@RequestParam("name") String name,
								  @RequestParam("ssnFront") String ssnFront,
								  @RequestParam("ssnBack") String ssnBack,
								  @RequestParam("tel") String tel,  // 여기서 phone을 tel로 수정
								  Model model) {
		// 파라미터 값 출력
		log.debug("Received name: " + name);
		log.debug("Received ssnFront: " + ssnFront);
		log.debug("Received ssnBack: " + ssnBack);
		log.debug("Received tel: " + tel);

		try {
			// 주민등록번호 뒷자리 암호화
			String encryptedSsnBack = encryptionService.encrypt(ssnBack);

			// 사용자 정보 조회
			SiteUser user = userService.findUserByDetails(name, ssnFront, encryptedSsnBack, tel);

			if (user != null) {
				model.addAttribute("username", user.getUsername());
				return "found_id";  // 아이디가 성공적으로 조회되면 해당 페이지로 이동
			} else {
				model.addAttribute("errorMessage", "일치하는 사용자 정보를 찾을 수 없습니다.");
				return "forgot_id";  // 에러 메시지를 보여주고 다시 아이디 찾기 페이지로 이동
			}
		} catch (Exception e) {
			model.addAttribute("errorMessage", "오류가 발생했습니다. 다시 시도하세요.");
			return "forgot_id";  // 예외 발생 시 다시 아이디 찾기 페이지로 이동
		}
	}





	@GetMapping("/logincheck")
	@ResponseBody
	public ResponseEntity<Boolean> checkAuthenticated(Principal principal) {
		// principal이 null이 아니면 사용자가 로그인된 상태
		if (principal != null) {
			return new ResponseEntity<>(true, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(false, HttpStatus.OK);
		}
	}

}