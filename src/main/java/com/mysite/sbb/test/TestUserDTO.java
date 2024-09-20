package com.mysite.sbb.test;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestUserDTO {
	

	private Long id;
	
	@Column(unique = true)
	private String name;
	
	private Long age;
	
	private List<TestProduct> products;
	
	

}
