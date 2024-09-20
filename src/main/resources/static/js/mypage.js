// dataContainer = document.getElementById('data-container');
// contextPath = dataContainer.getAttribute('data-my-variable');
// contextPath = contextPath === null ? "" : contextPath;
//
// console.log(contextPath);
//
// // 모달 창 열기/닫기 스크립트
// const messageModal = document.getElementById('messageModal');
// const openMessageModal = document.getElementById('openMessageModal');
// const closeMessageModal = document.getElementById('closeMessageModal');
//
// openMessageModal.addEventListener('click', function () {
//     messageModal.classList.remove('hidden');
// });
//
// closeMessageModal.addEventListener('click', function () {
//     messageModal.classList.add('hidden');
// });
//
// // 모달 창 외부 클릭 시 닫기
// window.addEventListener('click', function (event) {
//     if (event.target === messageModal) {
//         messageModal.classList.add('hidden');
//     }
// });
//
// // 친구 목록 모달 창 열기/닫기 스크립트
// const friendsModal = document.getElementById('friendsModal');
// const openFriendsModal = document.getElementById('openFriendsModal');
// const closeFriendsModal = document.getElementById('closeFriendsModal');
//
// openFriendsModal.addEventListener('click', function () {
//     friendsModal.classList.remove('hidden');
// });
//
// closeFriendsModal.addEventListener('click', function () {
//     friendsModal.classList.add('hidden');
// });
//
// // 모달 창 외부 클릭 시 닫기
// window.addEventListener('click', function (event) {
//     if (event.target === messageModal) {
//         messageModal.classList.add('hidden');
//     }
//     if (event.target === friendsModal) {
//         friendsModal.classList.add('hidden');
//     }
// });
//
// // 탭 전환 스크립트
// const receivedTab = document.getElementById('receivedTab');
// const sentTab = document.getElementById('sentTab');
// const sendMessageTab = document.getElementById('sendMessageTab');
// const receivedMessages = document.getElementById('receivedMessages');
// const sentMessages = document.getElementById('sentMessages');
// const sendMessageForm = document.getElementById('sendMessageForm');
//
// // 친구 목록 모달 창 탭 전환 스크립트
// const friendsListTab = document.getElementById('friendsListTab');
// const receivedRequestsTab = document.getElementById('receivedRequestsTab');
// const sendRequestsTab = document.getElementById('sendRequestsTab');
//
// const friendsListContent = document.getElementById('friendsList');
// const receivedRequestsContent = document.getElementById('receivedRequestsContent');
// const sendRequestsContent = document.getElementById('sendRequestsContent');
//
// friendsListTab.addEventListener('click', function () {
//     friendsListContent.classList.remove('hidden');
//     receivedRequestsContent.classList.add('hidden');
//     sendRequestsContent.classList.add('hidden');
//
//     friendsListTab.classList.add('bg-gray-200');
//     receivedRequestsTab.classList.remove('bg-gray-200');
//     sendRequestsTab.classList.remove('bg-gray-200');
// });
//
//
// receivedTab.addEventListener('click', function () {
//     receivedMessages.classList.remove('hidden');
//     sentMessages.classList.add('hidden');
//     sendMessageForm.classList.add('hidden');
//     receivedTab.classList.add('bg-gray-200');
//     sentTab.classList.remove('bg-gray-200');
//     sendMessageTab.classList.remove('bg-gray-200');
// });
//
// sentTab.addEventListener('click', function () {
//     sentMessages.classList.remove('hidden');
//     receivedMessages.classList.add('hidden');
//     sendMessageForm.classList.add('hidden');
//     sentTab.classList.add('bg-gray-200');
//     receivedTab.classList.remove('bg-gray-200');
//     sendMessageTab.classList.remove('bg-gray-200');
// });
//
// sendMessageTab.addEventListener('click', function () {
//     sendMessageForm.classList.remove('hidden');
//     receivedMessages.classList.add('hidden');
//     sentMessages.classList.add('hidden');
//     sendMessageTab.classList.add('bg-gray-200');
//     receivedTab.classList.remove('bg-gray-200');
//     sentTab.classList.remove('bg-gray-200');
// });
//
// receivedRequestsTab.addEventListener('click', function () {
//     receivedRequestsContent.classList.remove('hidden');
//     friendsListContent.classList.add('hidden');
//     sendRequestsContent.classList.add('hidden');
//
//     receivedRequestsTab.classList.add('bg-gray-200');
//     friendsListTab.classList.remove('bg-gray-200');
//     sendRequestsTab.classList.remove('bg-gray-200');
//
//     // 받은 친구 요청을 서버에서 가져오는 로직
//     fetch(`${contextPath}/friends/received`, {
//         method: 'GET',
//         headers: {
//             [csrfHeader]: csrfToken // CSRF 토큰을 헤더에 추가
//         }
//     })
//         .then(response => response.json())
//         .then(data => {
//             const receivedRequestsList = document.getElementById('receivedRequestsList');
//             receivedRequestsList.innerHTML = ''; // 기존 리스트 초기화
//
//             if (data.length === 0) {
//                 receivedRequestsList.innerHTML = '<li class="py-2 border-b text-center text-gray-600">받은 친구 신청이 없습니다.</li>';
//             } else {
//                 data.forEach(request => {
//                     const listItem = document.createElement('li');
//                     listItem.className = 'py-2 border-b flex justify-between items-center';
//                     listItem.innerHTML = `
//                 <span>${request.friendUsername} - ${request.friendMessage}</span>
//                 <div>
//                     <button class="accept-btn bg-green-500 text-white px-4 py-1 rounded mr-2" data-request-id="${request.id}">수락</button>
//                     <button class="reject-btn bg-red-500 text-white px-4 py-1 rounded" data-request-id="${request.id}">거절</button>
//                 </div>
//             `;
//                     receivedRequestsList.appendChild(listItem);
//                 });
//
//                 // 수락 버튼 이벤트 리스너 추가
//                 document.querySelectorAll('.accept-btn').forEach(button => {
//                     button.addEventListener('click', function () {
//                         const requestId = this.getAttribute('data-request-id');
//                         fetch(`${contextPath}/friends/accept?requestId=${requestId}`, {
//                             method: 'POST',
//                             headers: {
//                                 'Content-Type': 'application/json',
//                                 [csrfHeader]: csrfToken // CSRF 토큰을 헤더에 추가
//                             }
//                         })
//                             .then(response => {
//                                 if (response.ok) {
//                                     alert('친구 요청을 수락했습니다.');
//                                     this.closest('li').remove(); // 리스트에서 해당 항목 제거
//                                 } else {
//                                     alert('친구 요청 수락에 실패했습니다.');
//                                 }
//                             })
//                             .catch(error => {
//                                 console.error('Error:', error);
//                             });
//                     });
//                 });
//
//                 // 거절 버튼 이벤트 리스너 추가
//                 document.querySelectorAll('.reject-btn').forEach(button => {
//                     button.addEventListener('click', function () {
//                         const requestId = this.getAttribute('data-request-id');
//                         fetch(`${contextPath}/friends/reject?requestId=${requestId}`, {
//                             method: 'POST',
//                             headers: {
//                                 'Content-Type': 'application/json',
//                                 [csrfHeader]: csrfToken // CSRF 토큰을 헤더에 추가
//                             }
//                         })
//                             .then(response => {
//                                 if (response.ok) {
//                                     alert('친구 요청을 거절했습니다.');
//                                     this.closest('li').remove(); // 리스트에서 해당 항목 제거
//                                 } else {
//                                     alert('친구 요청 거절에 실패했습니다.');
//                                 }
//                             })
//                             .catch(error => {
//                                 console.error('Error:', error);
//                             });
//                     });
//                 });
//             }
//         })
//         .catch(error => console.error('Error:', error));
// });
//
// // 친구 목록 불러오기 및 표시
// friendsListTab.addEventListener('click', function () {
//     friendsListContent.classList.remove('hidden');
//     receivedRequestsContent.classList.add('hidden');
//     sendRequestsContent.classList.add('hidden');
//
//     friendsListTab.classList.add('bg-gray-200');
//     receivedRequestsTab.classList.remove('bg-gray-200');
//     sendRequestsTab.classList.remove('bg-gray-200');
//
//     // 친구 목록을 서버에서 가져오는 로직
//     fetch(`${contextPath}/friends/list`, {
//         method: 'GET',
//         headers: {
//             [csrfHeader]: csrfToken // CSRF 토큰을 헤더에 추가
//         }
//     })
//         .then(response => response.json())
//         .then(data => {
//             const friendsList = document.getElementById('friendsList');
//             friendsList.innerHTML = ''; // 기존 리스트 초기화
//
//             if (data.length === 0) {
//                 friendsList.innerHTML = '<li class="py-2 border-b text-center text-gray-600">친구가 없습니다.</li>';
//             } else {
//                 data.forEach(friend => {
//                     const listItem = document.createElement('li');
//                     listItem.className = 'py-2 border-b flex justify-between items-center';
//                     listItem.innerHTML = `
//                 <span>${friend.username}</span>
//                 <button class="start-chat-btn bg-blue-500 text-white px-4 py-1 rounded">대화 시작</button>
//                 <button class="wishlist-btn bg-blue-500 text-white px-4 py-1 rounded" data-friend-id="${friend.id}">위시리스트 방문</button>
//             `;
//                     friendsList.appendChild(listItem);
//                 });
//
//                 // 위시리스트 방문 버튼에 이벤트 리스너 추가
//                 document.querySelectorAll('.wishlist-btn').forEach(button => {
//                     button.addEventListener('click', function () {
//                         const friendId = this.getAttribute('data-friend-id');
//                         window.location.href = `${contextPath}/wishlist/${friendId}`;
//                     });
//                 });
//             }
//         })
//         .catch(error => console.error('Error:', error));
// });
//
// // 쪽지 전송
// const sendMessageButton = document.getElementById('sendMessageButton');
// sendMessageButton.addEventListener('click', function () {
//     const recipient = document.getElementById('recipient').value;
//     const title = document.getElementById('title').value;
//     const content = document.getElementById('content').value;
//
//     const messageData = {
//         recipient: recipient,
//         title: title,
//         content: content
//     };
//
//     fetch(`${contextPath}/messages/send`, {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json',
//             [csrfHeader]: csrfToken // CSRF 토큰을 헤더에 추가
//         },
//         body: JSON.stringify(messageData)
//     })
//         .then(response => {
//             if (response.ok) {
//                 alert('메시지가 성공적으로 전송되었습니다.');
//                 messageModal.classList.add('hidden'); // 메시지 전송 후 모달 닫기
//             } else if (response.status === 404) {
//                 console.log("수신자: ", recipient)
//                 console.log("messageData", messageData)
//                 console.log("contextPath", contextPath)
//                 alert('수신자를 찾을 수 없습니다.');
//             } else {
//                 alert('메시지 전송에 실패했습니다.');
//             }
//         })
//         .catch(error => {
//             console.error('Error:', error);
//             alert('메시지 전송 중 오류가 발생했습니다.');
//         });
// });
//
// // 받은 쪽지
// document.addEventListener('DOMContentLoaded', function () {
//     const username = document.getElementById('hiddenIdValue').value; // userId를 username으로 사용
//     const receivedMessagesBody = document.getElementById('receivedMessagesBody');
//     const miniMessageModal = document.getElementById('miniMessageModal');
//     const modalSender = document.getElementById('modalSender');
//     const modalContent = document.getElementById('modalContent');
//     const closeMiniModal = document.getElementById('closeMiniModal');
//
//     fetch(`${contextPath}/messages/received?username=${username}`)
//         .then(response => {
//             if (response.ok) {
//                 return response.json();
//             } else {
//                 throw new Error('Failed to load received messages.');
//             }
//         })
//         .then(messages => {
//             receivedMessagesBody.innerHTML = ''; // 기존 내용 제거
//             if (messages.length === 0) {
//                 const noMessageElement = document.createElement('tr');
//                 noMessageElement.innerHTML = `
//             <td colspan="3" class="py-2 px-4 text-center text-gray-600">받은 쪽지가 없습니다.</td>
//         `;
//                 receivedMessagesBody.appendChild(noMessageElement);
//             } else {
//                 messages.forEach(message => {
//                     const messageRow = document.createElement('tr');
//                     messageRow.className = 'border-b cursor-pointer'; // 클릭 가능하도록 커서 변경
//
//                     const senderElement = document.createElement('td');
//                     senderElement.className = 'py-2 px-4';
//                     senderElement.textContent = message.senderUsername;
//
//                     const titleElement = document.createElement('td');
//                     titleElement.className = 'py-2 px-4 truncate max-w-xs';
//                     titleElement.textContent = message.title;
//
//                     const contentElement = document.createElement('td');
//                     contentElement.className = 'py-2 px-4 truncate max-w-sm';
//                     contentElement.textContent = message.content;
//
//                     messageRow.appendChild(senderElement);
//                     messageRow.appendChild(titleElement);
//                     messageRow.appendChild(contentElement);
//
//                     // 클릭 이벤트 리스너 추가
//                     messageRow.addEventListener('click', function () {
//                         modalSender.textContent = `작성자: ${message.senderUsername}`;
//                         modalContent.textContent = `내용: ${message.content}`;
//                         miniMessageModal.classList.remove('hidden');
//                     });
//
//                     receivedMessagesBody.appendChild(messageRow);
//                 });
//             }
//         })
//         .catch(error => {
//             console.error('Error:', error);
//             const errorMessageElement = document.createElement('tr');
//             errorMessageElement.innerHTML = `
//         <td colspan="3" class="py-2 px-4 text-center text-red-500">쪽지를 불러올 수 없습니다.</td>
//     `;
//             receivedMessagesBody.appendChild(errorMessageElement);
//         });
//
//     // 모달 닫기 버튼 이벤트 리스너
//     closeMiniModal.addEventListener('click', function () {
//         miniMessageModal.classList.add('hidden');
//     });
//
//     // 모달 창 외부 클릭 시 닫기
//     window.addEventListener('click', function (event) {
//         if (event.target === miniMessageModal) {
//             miniMessageModal.classList.add('hidden');
//         }
//     });
// });
//
// // 보낸 쪽지
// document.addEventListener('DOMContentLoaded', function () {
//     const username = document.getElementById('hiddenIdValue').value; // userId를 username으로 사용
//     const sentMessagesBody = document.getElementById('sentMessagesBody');
//     const miniSentMessageModal = document.getElementById('miniSentMessageModal');
//     const modalSentReceiver = document.getElementById('modalSentReceiver');
//     const modalSentContent = document.getElementById('modalSentContent');
//     const closeMiniSentModal = document.getElementById('closeMiniSentModal');
//
//     fetch(`${contextPath}/messages/sent?username=${username}`)
//         .then(response => {
//             if (response.ok) {
//                 return response.json();
//             } else {
//                 throw new Error('Failed to load sent messages.');
//             }
//         })
//         .then(messages => {
//             sentMessagesBody.innerHTML = ''; // 기존 내용 제거
//             if (messages.length === 0) {
//                 const noMessageElement = document.createElement('tr');
//                 noMessageElement.innerHTML = `
//             <td colspan="3" class="py-2 px-4 text-center text-gray-600">보낸 쪽지가 없습니다.</td>
//         `;
//                 sentMessagesBody.appendChild(noMessageElement);
//             } else {
//                 messages.forEach(message => {
//                     const messageRow = document.createElement('tr');
//                     messageRow.className = 'border-b cursor-pointer'; // 클릭 가능하도록 커서 변경
//
//                     const receiverElement = document.createElement('td');
//                     receiverElement.className = 'py-2 px-4';
//                     receiverElement.textContent = message.receiverUsername;
//
//                     const titleElement = document.createElement('td');
//                     titleElement.className = 'py-2 px-4 truncate max-w-xs';
//                     titleElement.textContent = message.title;
//
//                     const contentElement = document.createElement('td');
//                     contentElement.className = 'py-2 px-4 truncate max-w-sm';
//                     contentElement.textContent = message.content;
//
//                     messageRow.appendChild(receiverElement);
//                     messageRow.appendChild(titleElement);
//                     messageRow.appendChild(contentElement);
//
//                     // 클릭 이벤트 리스너 추가
//                     messageRow.addEventListener('click', function () {
//                         modalSentReceiver.textContent = `받은 사람: ${message.receiverUsername}`;
//                         modalSentContent.textContent = `내용: ${message.content}`;
//                         miniSentMessageModal.classList.remove('hidden');
//                     });
//
//                     sentMessagesBody.appendChild(messageRow);
//                 });
//             }
//         })
//         .catch(error => {
//             console.error('Error:', error);
//             const errorMessageElement = document.createElement('tr');
//             errorMessageElement.innerHTML = `
//         <td colspan="3" class="py-2 px-4 text-center text-red-500">쪽지를 불러올 수 없습니다.</td>
//     `;
//             sentMessagesBody.appendChild(errorMessageElement);
//         });
//
//     // 모달 닫기 버튼 이벤트 리스너
//     closeMiniSentModal.addEventListener('click', function () {
//         miniSentMessageModal.classList.add('hidden');
//     });
//
//     // 모달 창 외부 클릭 시 닫기
//     window.addEventListener('click', function (event) {
//         if (event.target === miniSentMessageModal) {
//             miniSentMessageModal.classList.add('hidden');
//         }
//     });
// });
