package com.mysite.sbb.friend;

import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 친구 추가한 유저와의 다대일 관계
    @ManyToOne
    @JoinColumn(name = "user_id")
    private SiteUser user; // 이 필드는 친구를 추가한 사용자를 나타냅니다.

    // 친구의 userId
    @ManyToOne
    @JoinColumn(name = "friend_user_id")
    private SiteUser friend; // 친구가 되는 사용자를 참조합니다.


    public Friend(SiteUser user, SiteUser friend) {
        this.user = user;
        this.friend = friend;
    }
}
