// CSRF 토큰 및 헤더를 가져오는 코드
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');


// 모달 열기
function openModal() {
    document.getElementById("shareModal").style.display = "flex";
    loadCategoriesAndProducts();
}

// 모달 닫기
function closeModal() {
    document.getElementById("shareModal").style.display = "none";
}

// 개별 공유 모달 열기
function openIndividualShareModal(productId, productImage, productTitle, productLink) {
    const modal = document.getElementById('individualShareModal');
    const shareLinkInput = document.getElementById('shareLinkInput');
    const shareLinkImage = document.getElementById('shareLinkImage');
    const shareLinkTitle = document.getElementById('shareLinkTitle');
    const shareLinkLink = document.getElementById('addProductLink-' + productId).value;

    // 개별 상품 페이지의 링크 설정
    const shareLink = `${window.location.origin}${contextPath}/wishlist/products/detail?productId=${productId}`;

    shareLinkInput.value = shareLink; // 공유 링크로 상품 페이지 URL 설정
    shareLinkImage.value = productImage; // 상품의 이미지 URL 설정
    shareLinkTitle.value = productTitle; // 상품의 제목 설정
    shareLinkInput.dataset.productId = productId; // 상품 ID 설정
    shareLinkLink.value = productLink

    modal.style.display = 'flex'; // 모달을 표시
}


// 개별 공유 모달 닫기
function closeIndividualShareModal() {
    document.getElementById("individualShareModal").style.display = "none";
}

// 링크 복사
function copyLink() {
    const shareLinkInput = document.getElementById('shareLinkInput');
    shareLinkInput.select();
    shareLinkInput.setSelectionRange(0, 99999); // 모바일 대응
    document.execCommand("copy");
    alert("링크가 복사되었습니다: " + shareLinkInput.value);
}


	
	function shareKakaoLinks() {
	    const productId = document.getElementById('shareLinkInput').dataset.productId;
	    const productImage = document.getElementById('shareLinkImage').value;
	    const productTitleHtml = document.getElementById('shareLinkTitle').value;
	    const productLink = document.getElementById('shareLinkInput').value;

        const productTitle = stripHtmlTags(productTitleHtml)
	    console.log("모달 펑션 productlink", productLink)

	    Kakao.Link.sendDefault({
	        objectType: 'feed',
	        content: {
	            title: productTitle,
	            description: '상품 설명',
	            imageUrl: productImage,
	            link: {
	                mobileWebUrl: productLink,
	                webUrl: productLink
	            }
	        }
	    });


    // 카카오톡 공유버튼 클릭 시 Best Product에 데이터 전송
    fetch(`${contextPath}/admin/bestproduct/add`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            productId: productId,
            title: productTitle,
            image: productImage,
            link: productLink
        })
    })
        .then(response => {
            console.log('Response URL:', response.url);  // 응답 URL 출력
            console.log('Response status:', response.status);  // 응답 상태 코드 출력

            if (!response.ok) {
                return response.text().then(text => {
                    console.error('Response text:', text);
                    throw new Error('Network response was not ok: ' + response.statusText);
                });
            }

            const contentType = response.headers.get('content-type');
            if (!contentType || !contentType.includes('application/json')) {
                return response.text().then(text => {
                    console.error('Response is not JSON:', text);
                    throw new SyntaxError('Response was not JSON');
                });
            }

            return response.json();
        })
        .then(data => {
            console.log(data);
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

// 공유하기 버튼의 카테고리와 상품 로드
function loadCategoriesAndProducts() {
    const userId = document.getElementById('userId').value; // 사용자 아이디 가져오기

    fetch(`${contextPath}/wishlist/${userId}/categories`)
        .then(response => response.json())
        .then(categories => {
            const categoryProductList = document.getElementById('categoryProductList');
            categoryProductList.innerHTML = '';

            categories.forEach(category => {
                const categoryDiv = document.createElement('div');
                categoryDiv.className = 'category border p-4 rounded-lg mb-4';

                const categoryTitle = document.createElement('h4');
                categoryTitle.className = 'text-xl font-bold mb-2 flex items-center'; // flex와 items-center 클래스를 추가하여 수평 정렬
                categoryTitle.textContent = category.name; // 텍스트 콘텐츠로 설정

                // 상품이 있는 경우에만 체크박스와 텍스트 추가
                if (category.products.length > 0) {
                    const label = document.createElement('label');
                    label.className = 'flex items-center ml-4';
                    label.innerHTML = `<span class="mr-2">리스트 추가</span><input type="checkbox" class="form-checkbox category-checkbox">`;
                    categoryTitle.appendChild(label);
                }

                categoryDiv.appendChild(categoryTitle);

                const productList = document.createElement('ul'); // ul 태그로 상품 리스트 생성
                productList.className = 'space-y-2';
                category.products.forEach(product => {
                    const productItem = document.createElement('li');
                    productItem.id = `product-${product.id}`; // 고유 ID 설정
                    productItem.className = 'product-item border p-4 rounded-lg bg-gray-100 flex justify-between items-center';

                    const productInfo = document.createElement('div');
                    productInfo.className = 'product-info';

                    const productImage = document.createElement('img');
                    productImage.src = product.image;
                    productImage.id = 'miniImage';
                    productImage.className = 'w-16 h-16 object-cover rounded mr-4';

                    const productTitle = document.createElement('span');
                    productTitle.className = 'product-title font-bold';
                    productTitle.textContent = truncateText(product.title, 30);

                    const productPrice = document.createElement('span');
                    productPrice.className = 'product-price text-gray-500 ml-2';
                    productPrice.textContent = product.lprice.toLocaleString() + ' 원';

                    productInfo.appendChild(productImage);
                    productInfo.appendChild(productTitle);
                    productInfo.appendChild(productPrice);

                    const productMeta = document.createElement('div');
                    productMeta.className = 'product-meta flex items-center';

                    const productState = document.createElement('span');
                    productState.className = `product-state ${product.state} px-2 py-1 rounded ${product.state === 'public' ? 'bg-green-500 text-white' : 'bg-red-500 text-white'}`;
                    productState.textContent = product.state === 'public' ? '공개' : '비공개';

                    const shareCheckbox = document.createElement('label');
                    shareCheckbox.className = 'share-checkbox flex items-center ml-2';
                    shareCheckbox.innerHTML = `<input type="checkbox" class="mr-2 product-checkbox" checked> 공유여부`;

                    const shareButton = document.createElement('button');
                    shareButton.className = 'share-button bg-blue-500 text-white px-4 py-2 rounded ml-2';
                    shareButton.textContent = '개별공유';
                    shareButton.onclick = () => openIndividualShareModal(product.id, product.image, product.title, product.link);

                    productMeta.appendChild(productState);
                    productMeta.appendChild(shareCheckbox);
                    productMeta.appendChild(shareButton);

                    productItem.appendChild(productInfo);
                    // productItem.appendChild(productState); // 공개/비공개 상태를 productInfo 위에 추가
                    productItem.appendChild(productMeta);
                    productList.appendChild(productItem);
                });

                categoryDiv.appendChild(productList);
                categoryProductList.appendChild(categoryDiv);
            });
        })
        .catch(error => {
            console.error('Error loading categories and products:', error);
        });
}

// 텍스트 길이 제한 함수
function truncateText(text, maxLength) {
    const tempElement = document.createElement('div');
    tempElement.innerHTML = text;
    const plainText = tempElement.textContent || tempElement.innerText || '';
    if (plainText.length > maxLength) {
        return plainText.slice(0, maxLength) + '...';
    }
    return plainText;
}

// 링크 생성
function generateLink() {
    const userId = document.getElementById('userId').value;
    const link = `${window.location.origin}${contextPath}/wishlist/${userId}`;
    alert(`링크가 생성되었습니다: ${link}`);
}

// 카테고리 추가 함수
function addCategory(wishlistId) {
    const categoryNameInput = document.querySelector(`#wishlist-${wishlistId} input`);
    const categoryName = categoryNameInput.value;
    if (categoryName) {
        fetch(`${contextPath}/wishlist/${wishlistId}/categories`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=UTF-8'
            },
            body: JSON.stringify({name: categoryName})
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok ' + response.statusText);
                }
                return response.json();
            })
            .then(newCategory => {
                appendCategoryToWishlist(wishlistId, newCategory);
                categoryNameInput.value = '';  // 입력 필드 초기화
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error adding category: ' + error.message);
            });
    }
}

// goToCheckedCategories 함수 정의
function goToCheckedCategories() {
    const checkedCategories = [];

    // 체크된 카테고리 수집
    document.querySelectorAll('#categoryProductList .category').forEach(categoryDiv => {
        const categoryCheckbox = categoryDiv.querySelector('label input[type="checkbox"].category-checkbox');
        if (categoryCheckbox && categoryCheckbox.checked) { // 카테고리 체크박스가 체크된 경우에만 실행
            const categoryName = categoryDiv.querySelector('h4').textContent;
            const products = [];

            categoryDiv.querySelectorAll('.product-item').forEach(productItem => {
                const productCheckbox = productItem.querySelector('input[type="checkbox"].product-checkbox');
                if (productCheckbox && productCheckbox.checked) {
                    const product = {
                        title: productItem.querySelector('.product-title').textContent,
                        image: productItem.querySelector('img').src,
                        lprice: productItem.querySelector('.product-price').textContent,
                        // link: productItem.querySelector('.product-link').textContent
                        // categoryName: categoryName
                    };
                    products.push(product);
                }
            });

            if (products.length > 0) {
                checkedCategories.push({name: categoryName, products});
            }
        }
    });

    if (checkedCategories.length > 0) {
        // 체크된 카테고리를 포함하는 링크를 /checkedCategories.html로 생성
        const url = new URL(window.location.origin + contextPath + '/checkedCategories.html');
        url.searchParams.append('categories', JSON.stringify(checkedCategories));

        // 모달에 생성된 링크를 표시
        openCheckedCategoriesShareModal(url.toString());
    } else {
        alert('체크된 카테고리가 없습니다.');
    }
}


function openCheckedCategoriesShareModal(shareLink) {
    const modal = document.getElementById('checkedCategoriesShareModal');
    const shareLinkInput = document.getElementById('checkedCategoriesShareLinkInput');

    // 모달에 링크를 표시
    shareLinkInput.value = shareLink;

    // 모달 창을 표시
    modal.classList.remove('hidden');
}

function closeCheckedCategoriesShareModal() {
    document.getElementById('checkedCategoriesShareModal').classList.add('hidden');
}

function copyCheckedCategoriesLink() {
    const shareLinkInput = document.getElementById('checkedCategoriesShareLinkInput');
    shareLinkInput.select();
    document.execCommand('copy');
    alert('링크가 복사되었습니다: ' + shareLinkInput.value);
}

// 임시 버튼 클릭 시 체크된 상품 출력
document.querySelector('.bg-blue-500.text-white.px-4.py-2.rounded.mb-2[onclick="goToCheckedCategories()"]').addEventListener('click', function () {
    const checkedProducts = [];
    document.querySelectorAll('#categoryProductList .category').forEach(categoryDiv => {
        const categoryCheckbox = categoryDiv.querySelector('label input[type="checkbox"].category-checkbox');
        if (categoryCheckbox && categoryCheckbox.checked) { // 카테고리 체크박스가 체크된 경우에만 실행
            const categoryName = categoryDiv.querySelector('h4').textContent;
            const products = [];
            categoryDiv.querySelectorAll('.product-item').forEach(productItem => {
                const productCheckbox = productItem.querySelector('input[type="checkbox"].product-checkbox');
                if (productCheckbox && productCheckbox.checked) {
                    const product = {
                        title: productItem.querySelector('.product-title').textContent,
                        image: productItem.querySelector('img').src,
                        lprice: productItem.querySelector('.product-price').textContent,
                        categoryName: categoryName
                    };
                    products.push(product);
                }
            });
            if (products.length > 0) {
                checkedProducts.push({name: categoryName, products});
            }
        }
    });

    // 디버깅: 수집된 데이터 출력
    console.log('Checked Products:', checkedProducts);

    if (checkedProducts.length > 0) {
        const url = new URL(window.location.origin + contextPath + '/checkedCategories.html');
        url.searchParams.append('categories', JSON.stringify(checkedProducts));
        window.location.href = url.toString();
    } else {
        alert('체크된 상품이 없습니다.');
    }
});

// 친구 공유 모달 열기
function shareWithFriends() {
    const checkedCategories = collectCheckedCategories(); // 체크된 카테고리 수집

    if (checkedCategories.length > 0) {
        openFriendShareModal(); // 체크된 카테고리가 있으면 모달 열기
    } else {
        alert('체크된 카테고리가 없습니다.'); // 체크된 카테고리가 없으면 경고 메시지
    }
}

// 체크된 카테고리 수집 함수
function collectCheckedCategories() {
    const checkedCategories = [];

    document.querySelectorAll('#categoryProductList .category').forEach(categoryDiv => {
        const categoryCheckbox = categoryDiv.querySelector('label input[type="checkbox"].category-checkbox');
        if (categoryCheckbox && categoryCheckbox.checked) { // 카테고리 체크박스가 체크된 경우에만 실행
            const categoryName = categoryDiv.querySelector('h4').textContent;
            const products = [];

            categoryDiv.querySelectorAll('.product-item').forEach(productItem => {
                const productCheckbox = productItem.querySelector('input[type="checkbox"].product-checkbox');
                if (productCheckbox && productCheckbox.checked) {
                    const product = {
                        title: productItem.querySelector('.product-title').textContent,
                        image: productItem.querySelector('img').src,
                        lprice: productItem.querySelector('.product-price').textContent
                    };
                    products.push(product);
                }
            });

            if (products.length > 0) {
                checkedCategories.push({name: categoryName, products});
            }
        }
    });

    return checkedCategories;
}


// 친구 공유 모달 열기
function shareWithFriends() {
    const checkedCategories = collectCheckedCategories(); // 체크된 카테고리 수집

    if (checkedCategories.length > 0) {
        openFriendShareModal(); // 체크된 카테고리가 있으면 모달 열기
    } else {
        alert('체크된 카테고리가 없습니다.'); // 체크된 카테고리가 없으면 경고 메시지
    }
}

// 체크된 카테고리 수집 함수
function collectCheckedCategories() {
    const checkedCategories = [];

    document.querySelectorAll('#categoryProductList .category').forEach(categoryDiv => {
        const categoryCheckbox = categoryDiv.querySelector('label input[type="checkbox"].category-checkbox');
        if (categoryCheckbox && categoryCheckbox.checked) { // 카테고리 체크박스가 체크된 경우에만 실행
            const categoryName = categoryDiv.querySelector('h4').textContent;
            const products = [];

            categoryDiv.querySelectorAll('.product-item').forEach(productItem => {
                const productCheckbox = productItem.querySelector('input[type="checkbox"].product-checkbox');
                if (productCheckbox && productCheckbox.checked) {
                    const product = {
                        title: productItem.querySelector('.product-title').textContent,
                        image: productItem.querySelector('img').src,
                        lprice: productItem.querySelector('.product-price').textContent
                    };
                    products.push(product);
                }
            });

            if (products.length > 0) {
                checkedCategories.push({name: categoryName, products});
            }
        }
    });

    return checkedCategories;
}

// 친구 공유 모달 열기 함수
function openFriendShareModal() {
    const modal = document.getElementById('friendShareModal');
    modal.classList.remove('hidden');  // 모달 창을 보이게 함

    const friendListContainer = document.getElementById('friendList');
    friendListContainer.innerHTML = ''; // 이전 친구 목록 초기화

    // 친구 목록 가져오기
    fetch(`${contextPath}/friends/list`)  // 친구 목록을 가져오는 API 엔드포인트
        .then(response => response.json())
        .then(friends => {
            if (friends.length > 0) {
                friends.forEach(friend => {
                    const friendItem = document.createElement('div');
                    friendItem.classList.add('flex', 'justify-between', 'items-center', 'border-b', 'py-2');
                    friendItem.innerHTML = `
                        <span>${friend.username}</span>
                        <button class="bg-blue-500 text-white px-2 py-1 rounded" onclick="shareWithFriend('${friend.username}')">공유</button>
                    `;
                    friendListContainer.appendChild(friendItem);
                });
            } else {
                friendListContainer.innerHTML = '<p class="text-center text-gray-500">친구 목록이 없습니다.</p>';
            }
        })
        .catch(error => {
            console.error('Error fetching friends:', error);
            friendListContainer.innerHTML = '<p class="text-center text-red-500">친구 목록을 불러올 수 없습니다.</p>';
        });
}

// 특정 친구에게 체크된 카테고리 공유
function shareWithFriend(friendUsername) {
    const checkedCategories = collectCheckedCategories(); // 체크된 카테고리 수집

    if (checkedCategories.length > 0) {
        // 링크 생성
        const url = new URL(window.location.origin + contextPath + '/checkedCategories.html');
        url.searchParams.append('categories', JSON.stringify(checkedCategories));

        // 쪽지 전송
        sendLinkAsMessage(friendUsername, url.toString());
    } else {
        alert('체크된 카테고리가 없습니다.');
    }
}

// 쪽지로 링크를 전송하는 함수
function sendLinkAsMessage(friendUsername, shareLink) {
    const messageData = {
        recipient: friendUsername,
        title: '공유된 카테고리 링크',
        content: `아래 링크를 통해 카테고리를 확인하세요!:\n${shareLink}`
    };

    fetch(`${contextPath}/messages/send`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken // CSRF 토큰을 헤더에 추가
        },
        body: JSON.stringify(messageData)
    })
        .then(response => {
            if (response.ok) {
                alert(`${friendUsername}님에게 링크가 전송되었습니다.`);
            } else {
                throw new Error('메시지 전송에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error sending message:', error);
            alert('메시지 전송 중 오류가 발생했습니다.');
        });
}

// 모달 닫기
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('closeFriendShareModal').addEventListener('click', function () {
        const modal = document.getElementById('friendShareModal');
        modal.classList.add('hidden');
    });
});

function stripHtmlTags(html) {
    const div = document.createElement('div');
    div.innerHTML = html;
    return div.textContent || div.innerText || '';
}

// function shareKakaoLinks(categoryId) {
//     const userRealName = document.getElementById('userRealName').value
//     console.log('userRealName', userRealName)
//     const productId = document.getElementById(`categoryName-${categoryId}`).innerText;
//     console.log('productId: ', productId)
//
//     const message = `${userRealName}님의 ${productId} 카테고리`;
//     console.log(message)
//
//     // const productImage = document.getElementById('modalKakaoImage').value;
//     // console.log('productImage: ', productImage)
//     // const productTitle = document.getElementById('modalKakaoTitle').value;
//     // console.log('productTitle: ', productTitle)
//     const productLink = document.getElementById('checkedCategoriesShareLinkInput').value;
//     console.log('productLink: ', productLink)
//
//     Kakao.Link.sendDefault({
//         objectType: 'feed',
//         content: {
//             title: message,
//             description: userRealName + ' 님이 ' + productId + ' 카테고리를 공유했어요!',
//             imageUrl: '이미지!', // 이미지는 보류 (추후에 프로필 이미지로 교체)
//             link: {
//                 mobileWebUrl: productLink,
//                 webUrl: productLink
//             }
//         }
//     });
// }