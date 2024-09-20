package com.mysite.sbb.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mysite.sbb.cart.Cart;
import com.mysite.sbb.friend.Friend;
import com.mysite.sbb.message.Message;
import com.mysite.sbb.mycategory.MyCategory;
import com.mysite.sbb.wishlist.Wishlist;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	@Column(unique = true)
	private String email;

	private String name;

	@Column(nullable = false)
	private boolean enabled;  // 이메일 인증 여부 상태를 저장

	private String verificationToken;  // 이메일 인증 토큰

	private LocalDateTime tokenCreationTime;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private Wishlist wishlist;
	
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Cart cart;

	private LocalDateTime signupDate;

	private String role;

	@Lob
	@Column(nullable = true)
	private byte[] profileImage;

	private String profileImageFilename;

	// 새로운 주소 필드 추가
	private String address; // 기본 주소
	private String addressDetail; // 상세 주소
	private String zipcode; // 우편번호

	// 비밀번호 분실 질문과 답변 필드 추가
	@Column(name = "security_answer", nullable = false, columnDefinition = "varchar(255) default ''")
	private String securityAnswer;

	@Column(name = "security_question", nullable = false, columnDefinition = "varchar(255) default ''")
	private String securityQuestion;

	@Column(name = "tel", nullable = false, columnDefinition = "varchar(255) default ''")
	private String tel;

	// 주민등록번호 앞자리
	@Column(name = "rrn_front", nullable = false, columnDefinition = "varchar(6) default ''")
	private String rrnFront;

	// 주민등록번호 뒷자리 암호화 저장
	@Column(name = "rrn_back", nullable = false, columnDefinition = "varchar(255) default ''")
	private String rrnBack;

	@OneToMany(mappedBy = "receiver")
	private List<Message> receivedMessages;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Friend> friends;

	@OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Friend> friendOf;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MyCategory> myCategories;
}