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


// 뒤로가기 용 로딩할 때
document.addEventListener("DOMContentLoaded", function (event) {
    console.log("📌dom load 발생");


    const urlParams = new URLSearchParams(window.location.search);

    const action = document.body.dataset.action;


    const query = urlParams.get("query") || "";
    const page = parseInt(urlParams.get("page")) || 1;
    const categoryParam = urlParams.get("categoryIds");
    const categoryIds = categoryParam ? categoryParam.split(',') : [];
    const isClosedIncluded = urlParams.get("isClosedIncluded") === "true";
    const sort = urlParams.get("sort") || "recent";

    //최초로딩 판단
    const isInitialLoad = !query && categoryIds.length === 0 && page === 1;

    console.log("디버그:" + categoryIds);
    if (action === "board") {
        if(!isInitialLoad){
            console.log("패치됨");
            fetchBoardPosts(page,"", new Set(categoryIds));
            restoreBoardUI(categoryIds, page)
        }
        highlightSelectedButtons();
    } else if (action === "book") {
     //   fetchBookSearchResults(page, query);
    } else if (action === "contest") {
        fetchFilteredContests({
            page,
            categoryIds,
            isClosedIncluded,
            sort
        })
        restoreContestUI(categoryIds, isClosedIncluded, sort);
        highlightSelectedButtons();
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