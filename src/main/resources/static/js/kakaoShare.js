document.addEventListener('DOMContentLoaded', function () {
    Kakao.init('2d42f22e699985d6b2a23bc2b4938a99'); // Replace with your Kakao JavaScript key
});

// 카카오 공유
function shareKakao() {
    const userId = document.getElementById('userId').value;
    const link = `${contextPath}/${window.location.origin}/wishlist/${userId}`;

    const imageUrl = 'tempImage';

    Kakao.Link.sendDefault({
        objectType: 'feed',
        content: {
            title: '내 위시리스트',
            description: "공유하기 설명",
            imageUrl: imageUrl,
            link: {
                mobileWebUrl: link,
                webUrl: link,
            },
        },
        buttons: [
            {
                title: '웹으로 보기',
                link: {
                    mobileWebUrl: link,
                    webUrl: link
                },
            },
        ],
        installTalk: true,
    });
}

// 카카오톡으로 공유
function shareKakaoLink() {
    const shareLink = document.getElementById('shareLinkInput').value;
    const shareImage = document.getElementById('shareLinkImage').value;
    console.log('shareLink  ', shareLink)
    Kakao.Link.sendDefault({
        objectType: 'feed',
        content: {
            title: '내 위시리스트',
            description: "공유하기 설명",
            imageUrl: shareImage,
            link: {
                mobileWebUrl: shareLink,
                webUrl: shareLink,
            },
        },
        buttons: [
            {
                title: '웹으로 보기',
                link: {
                    mobileWebUrl: shareLink,
                    webUrl: shareLink
                },
            },
        ],
        installTalk: true,
    });
}
