package com.mysite.sbb.admin.CsCenterManagement;

import com.mysite.sbb.myhistory.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CsCenterManagementRepository extends JpaRepository<History, Long> {
    @Query("SELECT h FROM History h WHERE h.answerList IS EMPTY")
    List<History> findHistoriesWithoutAnswers();
}
