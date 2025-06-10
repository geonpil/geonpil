console.log("✅ contest.js loaded");


let page = 0;


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

        if (action === "board") {
            fetchBoardPosts(page);
        } else if (action === "book") {
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


// 뒤로가기 할때 parameter를 받아와 줌
window.addEventListener("popstate", function () {

    const urlParams = new URLSearchParams(window.location.search);

    console.log("📌 popstate 발생");
    const action = document.body.dataset.action;


    const query = urlParams.get("query") || "";
    const page = parseInt(urlParams.get("page")) || 1;
    const categoryParam = urlParams.get("categoryIds");
    const categoryIds = categoryParam ? categoryParam.split(',') : [];
    const isClosedIncluded = urlParams.get("isClosedIncluded") === "true";
    const sort = urlParams.get("sort") || "recent";


    console.log("디버그:" + categoryIds);
    if (action === "board") {
        fetchBoardPosts(page);
    } else if (action === "book") {
        fetchBookSearchResults(page, query);
    } else if (action === "contest") {
        fetchFilteredContests({
            page,
            categoryIds,
            isClosedIncluded,
            sort
        })
        restoreContestUI(categoryIds, isClosedIncluded, sort);
    }
});