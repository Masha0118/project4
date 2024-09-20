package com.mysite.sbb.myhistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Integer> {

    History findBySubject(String subject);

    History findBySubjectAndContent(String subject, String content);

    List<History> findBySubjectLike(String subject);

    Page<History> findAll(Pageable pageable);

    List<History> findTop10ByOrderByVotecountDesc();
    
    

    Page<History> findAll(Specification<History> spec, Pageable pageable);

    @Query("select "
            + "distinct q "
            + "from History q "
            + "left outer join SiteUser u1 on q.author=u1 "
            + "left outer join Answer a on a.history=q "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "where "
            + "   q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.username like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.username like %:kw% ")
    Page<History> findAllByKeyword(@Param("kw") String kw, Pageable pageable);


}
