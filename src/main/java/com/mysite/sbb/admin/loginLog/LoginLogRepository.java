package com.mysite.sbb.admin.loginLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    List<LoginLog> findAllByOrderByTimestampDesc();
}
