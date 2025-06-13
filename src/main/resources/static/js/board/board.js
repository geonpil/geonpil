function fetchBoardPosts(page = 1) {
    const params = new URLSearchParams();

    highlightSelectedButtons();

    if (selectedCategories.size > 0) {
        params.set("categoryIds", Array.from(selectedCategories).join(","));
    }

    const boardCode = new URLSearchParams(window.location.search).get("boardCode");
    if (boardCode) params.set("boardCode", boardCode);

    params.set("page", page);

    const newUrl = `/board/list?` + params.toString();
    history.pushState({}, '', newUrl);


    fetch("/board/list/fragment?" + params.toString())
        .then(res => res.text())
        .then(html => {
            document.getElementById("post-list").innerHTML = html;
        });

    fetch("/board/list/fragment/pagination?" + params.toString())
        .then(res => res.text())
        .then(html => {
            document.getElementById("pagination-area").innerHTML = html;
        });


}


window.addEventListener("popstate", () => {
    
    console.log("popstate 발생"); 
    
    const urlParams = new URLSearchParams(window.location.search);
    const page = parseInt(urlParams.get("page")) || 1;

    // ✅ 선택된 카테고리 복원 (선택사항)
    const categoryIdString = urlParams.get("categoryIds") || "";
    selectedCategories = new Set(categoryIdString.split(",").filter(Boolean));
    highlightSelectedButtons(); // UI 반영 함수

    fetchBoardPosts(page);
});