package com.mysite.sbb.user;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {

	@Size(min = 3, max = 25, message = "아이디는 3자 이상, 25자 이하여야 합니다.")
	@NotEmpty(message = "사용자ID는 필수항목입니다.")
	private String username;

	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하이어야 합니다.")
	@NotEmpty(message = "비밀번호는 필수항목입니다.")
	private String password1;

	@NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
	private String password2;
	
	@Size(min = 1, max = 4, message = "이름은 1자 이상, 50자 이하이어야 합니다.")
	@NotEmpty(message = "이름은 필수항목입니다.")
	private String name;

	@NotEmpty(message = "이메일은 필수항목입니다.")
	@Email
	private String email;
	
	@NotEmpty(message = "전화번호는 필수항목입니다.")
	private String tel;

	@NotEmpty(message = "비밀번호 분실 질문은 필수항목입니다.")
	private String securityQuestion;

	@NotEmpty(message = "비밀번호 분실 답변은 필수항목입니다.")
	private String securityAnswer;

	@NotEmpty(message = "주민등록번호 앞자리는 필수항목입니다.")
	 @Pattern(regexp = "^\\d{6}$", message = "주민등록번호 앞자리는 6자리 숫자여야 합니다.")
	private String rrnFront;  // 주민등록번호 앞자리

	@NotEmpty(message = "주민등록번호 뒷자리는 필수항목입니다.")
	@Pattern(regexp = "^\\d{7}$", message = "주민등록번호 뒷자리는 7자리 숫자여야 합니다.")
	private String rrnBack;  // 주민등록번호 뒷자리

	private UUID id = UUID.randomUUID();

	private String address;
	private String addressDetail;
	private String zipcode;
}