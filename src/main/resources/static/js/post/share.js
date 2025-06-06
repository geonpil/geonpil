function initKakaoShare({ title, description, imageUrl, pageUrl, containerId }) {
    if (!window.Kakao || !Kakao.isInitialized()) {
        Kakao.init('여기에_JavaScript_키');
    }

    Kakao.Share.createDefaultButton({
        container: containerId || '#kakao-link-btn',
        objectType: 'feed',
        content: {
            title: title,
            description: description,
            imageUrl: imageUrl,
            link: {
                mobileWebUrl: pageUrl,
                webUrl: pageUrl
            }
        },
        buttons: [
            {
                title: '자세히 보기',
                link: {
                    mobileWebUrl: pageUrl,
                    webUrl: pageUrl
                }
            }
        ]
    });
}

function copyLink(url) {
    navigator.clipboard.writeText(url || window.location.href).then(() => {
        alert("링크가 복사되었습니다!");
    }).catch(() => {
        alert("복사 실패");
    });
}
