package com.mysite.sbb.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByUsername(String username);
    Optional<SiteUser> findById(Long id);

    Optional<SiteUser> findByEmail(String email);
    Optional<SiteUser> findByVerificationToken(String token);

    // 사용자 정보로 아이디 조회
    Optional<SiteUser> findByNameAndRrnFrontAndRrnBackAndTel(String name, String rrnFront, String rrnBack, String tel);

    // userId로 username 조회
    @Query("SELECT u.username FROM SiteUser u WHERE u.id = :userId")
    Optional<String> findUsernameById(@Param("userId") Long userId);
}