package com.mysite.sbb.admin.apiUsage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {

}
