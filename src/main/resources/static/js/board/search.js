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

// 현재 페이지 타입 확인 (contest 또는 board)
function getPageType() {
    // body에 data-action 속성이 있는지 확인
    const bodyDataAction = document.body.getAttribute('data-action');
    if (bodyDataAction === 'contest') {
        return 'contest';
    }
    return 'board'; // 기본값은 board
}

// 검색 로직 함수로 분리
function searchHandler() {
    const keyword = document.getElementById("search-input").value.trim();
    const categoryIds = new URLSearchParams(window.location.search).get('categoryIds') || '';
    const boardCode = getBoardCodeFromUrl();
    const pageType = getPageType(); // 페이지 타입 확인
    const postArea = document.getElementById("contest-section") || document.getElementById("post-list");
    const searchType = document.getElementById("searchType").value.trim();



    if (!keyword) {
        alert("검색어를 입력해주세요.");
        return;
    }

    if (!boardCode) {
        console.error("게시판 코드를 찾을 수 없습니다.");
        return;
    }

    // 페이지 타입에 따라 API 경로 결정
    const apiPath = pageType === 'contest' ? '/api/search/contest' : '/api/search/board';

    // 페이지 타입에 따라 추가 파라미터 설정
    let apiUrl = `${apiPath}?keyword=${encodeURIComponent(keyword)}&page=1&boardCode=${boardCode}&categoryIds=${encodeURIComponent(categoryIds)}&searchType=${encodeURIComponent(searchType)}`;

    // 공모전의 경우 추가 파라미터 설정
    if (pageType === 'contest') {
        const isClosedIncluded = document.getElementById("isClosedIncluded")?.checked || false;
        const sortButtons = document.querySelectorAll(".sort-btn");
        let sort = "recent"; // 기본값

        for (const btn of sortButtons) {
            if (btn.classList.contains("active")) {
                sort = btn.getAttribute("data-sort");
                break;
            }
        }

        apiUrl += `&isClosedIncluded=${isClosedIncluded}&sort=${sort}`;
    }

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
        if (html.includes("class=\"no-result\"") || html.trim() === "") {
            if (postArea) {
                // 공모전 페이지와 일반 게시판 페이지의 결과 표시 영역 구조가 다를 수 있음
                if (pageType === 'contest') {
                    // 공모전 페이지에 맞는 결과 표시
                    const listArea = postArea.querySelector("#contest-list") || postArea;
                    listArea.innerHTML = "<div class='no-result'>검색 결과가 없습니다.</div>";
                } else {
                    // 일반 게시판 페이지에 맞는 결과 표시
                    postArea.innerHTML = "<div class='no-result'>검색 결과가 없습니다.</div>";
                }
            }
        } else {
            // HTML 결과를 페이지에 삽입
            if (postArea) {
                if (pageType === 'contest') {
                    // 공모전 페이지는 전체 컨테이너의 내부 컨텐츠를 교체하므로 조심해야 함
                    const combinedFragment = new DOMParser().parseFromString(html, 'text/html')
                                             .querySelector('[th\\:fragment="combinedContestFragment"]');
                    if (combinedFragment) {
                        postArea.innerHTML = '';
                        postArea.appendChild(combinedFragment);
                    } else {
                        postArea.innerHTML = html;
                    }
                } else {
                    // 일반 게시판 페이지
                    postArea.innerHTML = html;
                }
            }

            // URL 업데이트 (boardCode는 이미 URL에 있으므로 유지)
            const currentUrl = new URL(window.location.href);
            currentUrl.searchParams.set("categoryIds", categoryIds);
            currentUrl.searchParams.set('keyword', keyword);
            currentUrl.searchParams.set('page', '1');
            currentUrl.searchParams.set('searchType', searchType);

            if (pageType === 'contest') {
                const isClosedIncluded = document.getElementById("isClosedIncluded")?.checked || false;
                let sort = "recent";
                const sortButtons = document.querySelectorAll(".sort-btn");
                for (const btn of sortButtons) {
                    if (btn.classList.contains("active")) {
                        sort = btn.getAttribute("data-sort");
                        break;
                    }
                }
                currentUrl.searchParams.set('isClosedIncluded', isClosedIncluded);
                currentUrl.searchParams.set('sort', sort);
            }

            // 이벤트 바인딩
            if (pageType === 'contest') {
                bindContestCategoryClickEvents();
            }

            history.pushState(null, "", currentUrl.toString());
        }
    })
    .catch(error => {
        console.error("검색 요청 실패:", error);
        if (postArea) {
            postArea.innerHTML = "<div class='error'>검색 중 오류가 발생했습니다.</div>";
        }
    });
}


