document.getElementById("search-btn").addEventListener("click", searchHandler);

document.getElementById("search-input").addEventListener("keydown", (e) => {
    if(e.key ==="Enter"){
        e.preventDefault()
        searchHandler();
    }

})

// 검색 로직 함수로 분리
function searchHandler() {
    const keyword = document.getElementById("search-input").value.trim();
    const searchType = document.getElementById("searchType").value;
    const postArea = document.getElementById("post-list");

    if (!keyword) {
        alert("검색어를 입력해주세요.");
        return;
    }

    if (searchType === "titleContent") {
        fetch(`/api/search/board?keyword=${encodeURIComponent(keyword)}`, {
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
                }
            })
            .catch(error => {
                console.error("검색 요청 실패:", error);
                postArea.innerHTML = "<div class='error'>검색 중 오류가 발생했습니다.</div>";
            });
    }
}
