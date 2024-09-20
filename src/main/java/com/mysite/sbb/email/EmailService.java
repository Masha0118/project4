package com.mysite.sbb.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(String to, String subject, String text) {
		SimpleMailMessage message=new SimpleMailMessage();
		message.setTo(to);										//이메일을 전송할 상대방 계정
		message.setSubject(subject);							//전송할 이메일 제목
		message.setText(text);									//전송할 이메일 내용
		mailSender.send(message);								//전송
	}
	
	
	
	

}
