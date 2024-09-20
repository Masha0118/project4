package com.mysite.sbb.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {
	private String email;
	private String password;
}
