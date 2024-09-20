package com.mysite.sbb.friend;

import com.mysite.sbb.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findByRecipient(SiteUser recipient);
    boolean existsBySenderAndRecipient(SiteUser sender, SiteUser recipient);
}
