document.getElementById("search-btn").addEventListener("click", searchHandler);

document.getElementById("search-input").addEventListener("keydown", (e) => {
    if(e.key === "Enter"){
        e.preventDefault();
        searchHandler();
    }
});

function getBoardCodeFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('boardCode') || urlParams.get('board_code');
}

// 검색 로직 함수로 분리
function searchHandler() {
    const keyword = document.getElementById("search-input").value.trim();
    const categoryIds = new URLSearchParams(window.location.search).get('categoryIds') || '';
    const boardCode = getBoardCodeFromUrl();
    const postArea = document.getElementById("post-list");

    if (!keyword) {
        alert("검색어를 입력해주세요.");
        return;
    }

    if (!boardCode) {
        console.error("게시판 코드를 찾을 수 없습니다.");
        return;
    }

    fetch(`/api/search/board?keyword=${encodeURIComponent(keyword)}&page=1&boardCode=${boardCode}&categoryIds=${encodeURIComponent(categoryIds)}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then(response => {
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.text();
    })
    .then(html => {
        if (html.includes("class=\"no-result\"") || html.trim() === "") {
            postArea.innerHTML = "<div class='no-result'>검색 결과가 없습니다.</div>";
        } else {
            postArea.innerHTML = html;
            // URL 업데이트 (boardCode는 이미 URL에 있으므로 유지)
            const currentUrl = new URL(window.location.href);
            currentUrl.searchParams.set("categoryIds", categoryIds); // 카테고리 파라미터 초기화
            currentUrl.searchParams.set('keyword', keyword);
            currentUrl.searchParams.set('page', '1');
            history.pushState(null, "", currentUrl.toString());
        }
    })
    .catch(error => {
        console.error("검색 요청 실패:", error);
        postArea.innerHTML = "<div class='error'>검색 중 오류가 발생했습니다.</div>";
    });
}
