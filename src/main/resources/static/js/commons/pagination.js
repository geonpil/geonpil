console.log("✅ contest.js loaded");


let page = 1;


// 페이지를 클릭할 때 loading
document.addEventListener("click", function (e) {
    if (e.target.classList.contains("page-button")) {

        const urlParams = new URLSearchParams(window.location.search);

        page = parseInt(e.target.dataset.page);
        const action = e.target.dataset.action;
        const query = e.target.dataset.query || "";


        const categoryParam = urlParams.get("categoryIds");
        const categoryIds = categoryParam ? categoryParam.split(',') : [];
        const isClosedIncluded = urlParams.get("isClosedIncluded") === "true";
        const sort = urlParams.get("sort") || "recent";
        const keyword = urlParams.get("keyword") || "";


        if (action === "board") {
            fetchBoardPosts(page, keyword, new Set(categoryIds));
        } else if (action === "book" || action === "admin") {
            fetchBookSearchResults(page, query);
        } else if (action === "contest") {
            // 검색어 여부에 따라 다른 동작 수행
            if (keyword && keyword.trim() !== "") {
                // 검색 상태인 경우 검색 API 호출
                fetchContestSearchResults(page, keyword, categoryIds, isClosedIncluded, sort);
            } else {
                // 일반 목록 조회인 경우
                fetchFilteredContests({
                    page,
                    categoryIds,
                    isClosedIncluded,
                    sort
                });
            }
        }
    }
});


// 검색 결과 페이지네이션 처리 함수
function fetchContestSearchResults(page, keyword, categoryIds, isClosedIncluded, sort) {
    const boardCode = getBoardCodeFromUrl();

    if (!boardCode) {
        console.error("게시판 코드를 찾을 수 없습니다.");
        return;
    }

    // 검색 API URL 구성
    const apiUrl = `/api/search/contest?keyword=${encodeURIComponent(keyword)}&page=${page}&boardCode=${boardCode}` +
                   `&categoryIds=${categoryIds.join(',')}&isClosedIncluded=${isClosedIncluded}&sort=${sort}`;

    // URL 상태 업데이트
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set('page', page);
    history.pushState(null, "", currentUrl.toString());

    // API 호출
    fetch(apiUrl, {
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
        const contestSection = document.getElementById("contest-section") || document.querySelector(".board-container");
        if (contestSection) {
            const combinedFragment = new DOMParser().parseFromString(html, 'text/html')
                                      .querySelector('[th\\:fragment="combinedContestFragment"]');
            if (combinedFragment) {
                contestSection.innerHTML = '';
                contestSection.appendChild(combinedFragment);
            } else {
                contestSection.innerHTML = html;
            }

            // 이벤트 다시 바인딩
            if (typeof bindContestClickEvents === 'function') {
                bindContestClickEvents();
            }
        }
    })
    .catch(error => {
        console.error("검색 결과 로딩 실패:", error);
    });
}


// boardCode 가져오기 유틸리티 함수
function getBoardCodeFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('boardCode') || urlParams.get('board_code');
}

function highlightSelectedButtons() {
    document.querySelectorAll(".category-btn").forEach(btn => {
        const id = btn.dataset.id;
        if (id === "") {
            btn.classList.toggle("active", selectedCategories.size === 0);
        } else {
            btn.classList.toggle("active", selectedCategories.has(id));
        }
    });
}