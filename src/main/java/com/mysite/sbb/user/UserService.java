package com.mysite.sbb.user;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mysite.sbb.cart.Cart;
import com.mysite.sbb.cart.CartRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.email.EmailService;
import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.mycategory.MyCategoryRepository;
import com.mysite.sbb.wishlist.Wishlist;
import com.mysite.sbb.wishlist.WishlistRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	@Value("${app.base-url}")
	private String baseUrl;

	@Value("${server.servlet.context-path:}")
	private String contextPath;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final WishlistRepository wishlistRepository;
	private final MyCategoryRepository categoryRepository;
	private final EmailService emailService;
	private final EncryptionService encryptionService; // EncryptionService 주입
	private final CartRepository cartRepository;

	@Value("${file.profile-upload-dir}")
	private String uploadDir;

	public SiteUser create(String username, String email, String password, String name, String tel, String securityQuestion, String securityAnswer, String address, String addressDetail, String zipcode, String rrnFront, String rrnBack) {
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setSignupDate(LocalDateTime.now());
		user.setName(name);
		user.setTel(tel);
		user.setSecurityQuestion(securityQuestion);
		user.setSecurityAnswer(securityAnswer);
		user.setEnabled(false);
		user.setAddress(address);
		user.setAddressDetail(addressDetail);
		user.setZipcode(zipcode);

		// 주민등록번호 앞자리 저장
		user.setRrnFront(rrnFront);

		// 주민등록번호 뒷자리 암호화하여 저장
		try {
			String encryptedRrnBack = encryptionService.encrypt(rrnBack);
			user.setRrnBack(encryptedRrnBack);
		} catch (Exception e) {
			throw new RuntimeException("주민등록번호 뒷자리 암호화 중 오류가 발생했습니다.", e);
		}

		if ("admin".equals(username)) {
			user.setRole("ADMIN");
		} else {
			user.setRole("USER");
		}

		this.userRepository.save(user);

		// 기본 위시리스트와 카테고리 생성
		createDefaultWishlistAndCategory(user);

		// 회원가입 시 Cart 생성
		createCartForUser(user);

		// 이메일 인증 토큰 생성 및 이메일 전송
		String token = UUID.randomUUID().toString();
		user.setVerificationToken(token);
		user.setTokenCreationTime(LocalDateTime.now());
		userRepository.save(user);

//		System.out.println("확인 : " + baseUrl + contextPath);

		//배포버전
		  String confirmationUrl = baseUrl + contextPath + "/user/verify-email?token="
		  + token;
		 
		//개발버전
			/*
			 * String confirmationUrl = "http://localhost:8080" + contextPath +
			 * "/user/verify-email?token=" + token;
			 */
		emailService.sendEmail(user.getEmail(), "이메일 인증", "링크를 클릭하면 계정이 활성화 됩니다 : " + confirmationUrl);

		return user;
	}

	private void createCartForUser(SiteUser user) {
		Cart cart = new Cart();
		cart.setUser(user);
		cartRepository.save(cart);
	}

	private void createDefaultWishlistAndCategory(SiteUser user) {
		String wishlistName = user.getUsername() + "님의 위시리스트";

		Wishlist defaultWishlist = new Wishlist();
		defaultWishlist.setName(wishlistName);
		defaultWishlist.setUser(user);
		wishlistRepository.save(defaultWishlist);

		MyCategory basicCategory = new MyCategory();
		basicCategory.setName("기본 카테고리");
		basicCategory.setState("private");
		basicCategory.setUsername(user.getUsername());
		basicCategory.setWishlistId(defaultWishlist.getId());
		categoryRepository.save(basicCategory);

		user.setWishlist(defaultWishlist);
		userRepository.save(user);
	}

	public SiteUser getUser(String username) {
		Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
		if (siteUser.isPresent()) {
			return siteUser.get();
		} else {
			throw new DataNotFoundException("siteuser not found");
		}
	}

	public String validateVerificationToken(String token) {
		SiteUser user = userRepository.findByVerificationToken(token).orElse(null);
		if (user == null) {
			return "invalid";
		}

		// 토큰 유효성 검사
		if (!isTokenValid(user)) {
			return "expired";
		}

		user.setEnabled(true);
		user.setVerificationToken(null); // 사용 후 토큰 삭제
		user.setTokenCreationTime(null); // 토큰 생성 시간 삭제
		userRepository.save(user);

		return "valid";
	}

	private boolean isTokenValid(SiteUser user) {
		if (user.getTokenCreationTime() == null) {
			return false;
		}

		LocalDateTime tokenCreationTime = user.getTokenCreationTime();
		LocalDateTime now = LocalDateTime.now();
		long minutesBetween = ChronoUnit.MINUTES.between(tokenCreationTime, now);

		return minutesBetween < 30; // 30분 이내 유효
	}

	public SiteUser getUserById(Long id) {
		Optional<SiteUser> user = userRepository.findById(id);
		if (user.isPresent()) {
			return user.get();
		} else {
			throw new RuntimeException("User not found");
		}
	}

	// 모든 사용자를 반환하는 메서드 추가
	public List<SiteUser> getAll() {
		return userRepository.findAll();
	}

	public Long getCount() {
		return userRepository.count();
	}

	public Map<LocalDate, Long> getUserSignupStats() {
		List<SiteUser> users = userRepository.findAll();
		return users.stream()
				.collect(Collectors.groupingBy(user -> user.getSignupDate().toLocalDate(), Collectors.counting()));
	}

	// 기간에 따른 가입 통계 반환 메서드 (주 또는 월 단위)
	public Map<String, Long> getSignupStatsByPeriod(String period) {
		List<SiteUser> users = userRepository.findAll();
		if ("weekly".equals(period)) {
			WeekFields weekFields = WeekFields.of(Locale.getDefault());
			return users.stream()
					.collect(Collectors.groupingBy(
							user -> user.getSignupDate().getDayOfWeek().name(), // 요일로 그룹화
							Collectors.counting()
					));
		} else if ("monthly".equals(period)) {
			return users.stream()
					.collect(Collectors.groupingBy(
							user -> String.valueOf(user.getSignupDate().getDayOfMonth()), // 일자로 그룹화
							Collectors.counting()
					));
		}
		return Collections.emptyMap();
	}

	public SiteUser save(SiteUser user) {
		return userRepository.save(user);
	}

	public void deleteUserById(Long userId) {
		userRepository.deleteById(userId);
	}

	public SiteUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
//			System.out.println("현재 사용자는 인증되지 않았습니다.");
			return null;  // 인증되지 않은 경우 null 반환
		}

		String username = ((UserDetails) authentication.getPrincipal()).getUsername();
//		System.out.println("현재 로그인된 사용자: " + username);  // 디버그용 로그
		return getUserByUsername(username);
	}


	public SiteUser getUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}


	/**
	 * 비밀번호 변경 로직을 처리하는 메서드
	 */
	public void changePassword(SiteUser user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword)); // 비밀번호 암호화
		userRepository.save(user);
	}

	/**
	 * 사용자 아이디와 기존 비밀번호를 이용한 인증 및 비밀번호 변경
	 */
	public void changePassword(String username, String oldPassword, String newPassword) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("유효하지 않은 사용자 이름입니다.");
		}

		SiteUser user = getUser(username);

		// 기존 비밀번호가 일치하는지 확인
		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
		}

		// 새 비밀번호로 변경
		user.setPassword(passwordEncoder.encode(newPassword)); // 비밀번호 암호화
		userRepository.save(user);
	}

	// 사용자 프로필 이미지 경로를 반환하는 메서드
	public String getUserProfileImageFilename(SiteUser user) {
		return user.getProfileImageFilename();
	}

	// 회원가입 시 아이디 중복 체크
	public boolean isUsernameAvailable(String username) {
		return userRepository.findByUsername(username).isEmpty();
	}

	// 비밀번호 분실 후  비밀번호 재설정
	public void resetPassword(String username, String newPassword) {
		SiteUser user = getUserByUsername(username);
		if (user == null) {
			throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
		}

		// 새 비밀번호 암호화 및 저장
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	public SiteUser findUserByDetails(String name, String ssnFront, String encryptedSsnBack, String tel) {
		Optional<SiteUser> userOptional = userRepository.findByNameAndRrnFrontAndRrnBackAndTel(name, ssnFront, encryptedSsnBack, tel);
		if (userOptional.isPresent()) {
			return userOptional.get();
		} else {
			return null; // 일치하는 사용자가 없는 경우
		}
	}

	// userId로 username을 조회하는 메서드
	public Optional<String> getUsernameByUserId(Long userId) {
		return userRepository.findUsernameById(userId);
	}

}
