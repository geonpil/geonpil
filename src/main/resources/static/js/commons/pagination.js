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
        const keyword = urlParams.get("keyword")||"";


        if (action === "board") {
            fetchBoardPosts(page,keyword,new Set(categoryIds));
        } else if (action === "book"||action === "admin") {
            fetchBookSearchResults(page, query);
        } else if (action === "contest") {
            fetchFilteredContests({
                page,
                categoryIds,
                isClosedIncluded,
                sort
            })
        }
    }
});



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