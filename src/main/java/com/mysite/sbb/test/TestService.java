package com.mysite.sbb.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestService {
	
	@Autowired
	TestRepository testRepository;
	
	// 이용자 저장하기
	public TestUser saveUser(String name, Long age) {
		
		TestUser user = new TestUser();
		user.setName(name);
		user.setAge(age);
		return testRepository.save(user);
		
	}

	public List<TestUser> getAll(){
		return testRepository.findAll();
		
	}
	
	public void allDelete() {
		testRepository.deleteAll();
	}
	public void deleteById(Long id) {
		testRepository.deleteById(id);
	}
	
}
