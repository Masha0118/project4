package com.mysite.sbb.friend;

import com.mysite.sbb.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findByUserOrFriend(SiteUser user, SiteUser friend);
}
