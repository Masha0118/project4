package com.mysite.sbb.friend;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(@AuthenticationPrincipal UserDetails currentUser, @RequestBody FriendRequestDto requestDto) {
        // 현재 인증된 사용자의 정보를 가져옵니다.
        SiteUser sender = userService.getUserByUsername(currentUser.getUsername());
        if (sender == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current user not found");
        }

        // 친구 요청을 보낼 수신자의 정보를 가져옵니다.
        SiteUser recipient = userService.getUserByUsername(requestDto.getFriendUsername());
//        System.out.println("recipient?? " + recipient);
//        System.out.println(requestDto.getFriendUsername());
//        System.out.println("requestDTO ?? :" + requestDto);

        // 수신자가 없을 경우 404 반환
        if (recipient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // 친구 요청을 처리하고 결과 메시지를 반환합니다.
        String resultMessage = friendService.sendFriendRequest(sender, recipient, requestDto.getFriendMessage());

        if (resultMessage.equals("이미 친구 요청을 받은 회원입니다.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMessage);
        }

        return ResponseEntity.ok(resultMessage);
    }

    @GetMapping("/received")
    public ResponseEntity<List<FriendRequestDto>> getReceivedRequests(@AuthenticationPrincipal UserDetails currentUser) {
        SiteUser user = userService.getUserByUsername(currentUser.getUsername());
        List<FriendRequestDto> receivedRequests = friendService.getReceivedFriendRequests(user);
        return ResponseEntity.ok(receivedRequests);
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptFriendRequest(@RequestParam("requestId") Long requestId) {
        friendService.acceptFriendRequest(requestId);
        return ResponseEntity.ok("Friend request accepted");
    }

    @PostMapping("/reject")
    public ResponseEntity<String> rejectFriendRequest(@RequestParam("requestId") Long requestId) {
        friendService.rejectFriendRequest(requestId);
        return ResponseEntity.ok("Friend request rejected");
    }

    @GetMapping("/list")
    public ResponseEntity<List<FriendDto>> getFriendsList(@AuthenticationPrincipal UserDetails currentUser) {
        SiteUser user = userService.getUserByUsername(currentUser.getUsername());
        List<FriendDto> friendsList = friendService.getFriendsList(user);
        return ResponseEntity.ok(friendsList);
    }
}
