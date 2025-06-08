document.addEventListener("click", function (e) {
    if (e.target.classList.contains("page-button")) {
        const page = parseInt(e.target.dataset.page);
        const action = e.target.dataset.action;
        const query = e.target.dataset.query || "";

        if (action === "board") {
            fetchBoardPosts(page);
        } else if (action === "book") {
            fetchBookSearchResults(page, query);
        }
    }
});