function initSearchBar() {
    const searchInput = document.getElementById("searchInput");
    const searchButton = document.querySelector(".btn-primary");

    if (searchButton) {
        searchButton.addEventListener("click", () => {
            const query = searchInput.value.trim();
            if (!query) {
                alert("검색어를 입력해주세요.");
                return;
            }
            performSearch(query, 1);
        });
    }

    searchInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            const query = searchInput.value.trim();
            if (!query) {
                alert("검색어를 입력해주세요.");
                return;
            }
            performSearch(query, 1);
        }
    });
}

function performSearch(query, page = 1) {
    const encodedQuery = encodeURIComponent(query);

    if (window.location.pathname === '/' || window.location.pathname === '/main') {
        // 👉 메인화면일 경우 → 전체 이동
        window.location.href = `/api/search?query=${encodedQuery}&page=1`;
    } else {
        // 👉 검색결과 화면일 경우 → AJAX로 내용만 갱신
        const newUrl = `/api/search?query=${encodedQuery}&page=${page}`;
        history.pushState({}, '', newUrl);

        document.querySelector(".search-header h2").textContent = "검색어: " + query;


        // 1.검색결과 가져오기
        fetch(`/api/search/fragment/result?query=${encodedQuery}&page=${page}`)
            .then(res => res.text())
            .then(html => {
                document.querySelector("#book-list-area").innerHTML = html;
                window.scrollTo({ top: 0, behavior: 'smooth' });
            });
        // 2.페이지네이션 가져오기
        fetch(`/api/search/fragment/pagination?query=${encodedQuery}&page=${page}`)
            .then(res => res.text())
            .then(html => {
                document.querySelector("#pagination-area").innerHTML = html;
            });

    }
}

window.addEventListener("DOMContentLoaded", initSearchBar);