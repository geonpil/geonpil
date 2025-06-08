function fetchBoardPosts(page = 1) {
    const params = new URLSearchParams();

    highlightSelectedButtons();

    if (selectedCategories.size > 0) {
        params.set("categoryIds", Array.from(selectedCategories).join(","));
    }

    const boardCode = new URLSearchParams(window.location.search).get("boardCode");
    if (boardCode) params.set("boardCode", boardCode);

    params.set("page", page);

    const newUrl = `/api/search?query=${encodeURIComponent(query)}&page=${page}`;
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