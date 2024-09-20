package com.mysite.sbb.friend;

import com.mysite.sbb.user.SiteUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;

    public String sendFriendRequest(SiteUser sender, SiteUser recipient, String message) {
        // 먼저, 수신자가 이미 발신자에게 친구 요청을 보냈는지 확인합니다.
        if (friendRequestRepository.existsBySenderAndRecipient(recipient, sender)) {
            return "이미 친구 요청을 받은 회원입니다.";
        }

        // 새로운 친구 요청을 생성하고 저장합니다.
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setRecipient(recipient);
        friendRequest.setMessage(message);
        friendRequestRepository.save(friendRequest);

        return "Friend request sent successfully";
    }

    public boolean hasPendingFriendRequest(SiteUser sender, SiteUser recipient) {
        // sender와 recipient 조합으로 중복된 친구 요청이 있는지 확인합니다.
        return friendRequestRepository.existsBySenderAndRecipient(sender, recipient);
    }

    public List<FriendRequestDto> getReceivedFriendRequests(SiteUser currentUser) {
        List<FriendRequest> requests = friendRequestRepository.findByRecipient(currentUser);

        return requests.stream()
                .map(request -> new FriendRequestDto(
                        request.getId(),
                        request.getSender().getUsername(),
                        request.getMessage(),
                        request.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        // 친구로 등록
        Friend friend = new Friend();
        friend.setUser(request.getSender());
        friend.setFriend(request.getRecipient());
        friendRepository.save(friend);

        // 친구 요청 삭제
        friendRequestRepository.delete(request);
    }

    @Transactional
    public void rejectFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        // 친구 요청 삭제
        friendRequestRepository.delete(request);
    }

    public List<FriendDto> getFriendsList(SiteUser currentUser) {
        List<Friend> friends = friendRepository.findByUserOrFriend(currentUser, currentUser);

        return friends.stream()
                .map(friend -> {
                    // 현재 사용자가 USER_ID에 해당하는 경우 FRIEND_USER_ID를, 반대의 경우 USER_ID를 반환
                    SiteUser friendUser = friend.getUser().equals(currentUser) ? friend.getFriend() : friend.getUser();
                    return new FriendDto(friendUser.getId(), friendUser.getUsername());
                })
                .collect(Collectors.toList());
    }
}
