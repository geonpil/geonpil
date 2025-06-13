const selectedCategories = new Set();
let currentSort = "recent";
let isClosedIncluded =  document.getElementById("isClosedIncluded").checked;
let boardCode = document.getElementById("boardCode").value;

function fetchFilteredContests({ page = 1, categoryIds = [], isClosedIncluded = false, sort = "recent"}) {
    const params = new URLSearchParams();

    if (categoryIds.length > 0) {
        params.set("categoryIds", categoryIds.join(","));
    }


    params.set("boardCode", boardCode);
    params.set("page", page);
    params.set("sort", sort);
    params.set("isClosedIncluded", isClosedIncluded);

    console.log("마감일"+params.toString());

    saveFilterStateToHistory(page, categoryIds, isClosedIncluded, sort)

    fetch("/contest/list/fragment?" + params.toString())
        .then(res => res.text())
        .then(html => {
            document.getElementById("contest-list").innerHTML = html;
            highlightSelectedButtons();
            bindContestClickEvents();
        })
        .catch(err => console.error("게시글 불러오기 실패", err));


    fetch("/contest/list/fragment/pagination?" + params.toString())
        .then(res => res.text()
            .then(html => {
                document.getElementById("pagination-area").innerHTML = html;
            }))
        .catch(err => console.error("페이지네이션 불러오기 실패", err));
}



document.addEventListener("DOMContentLoaded", function () {

    bindContestClickEvents();

    // ✅ '전체' 버튼에 .active 클래스 부여
    const allBtn = document.querySelector('.category-btn[data-id=""]');
    if (allBtn && selectedCategories.size === 0) {
        allBtn.classList.add("active");
    }

    // ✅ 정렬 버튼 상태 동기화 (기존 강제지정 삭제)
    document.querySelectorAll(".sort-btn").forEach(btn => {
        const sortType = btn.dataset.sort;
        btn.classList.toggle("active", sortType === currentSort);
    });
});

function bindContestClickEvents() {
    document.querySelectorAll(".contest-row").forEach(row => {
        row.addEventListener("click", function () {
            const id = this.dataset.id;
/*            if (id) {

                // ✅ 선택된 필터/상태를 다시 push (중복이더라도 안전)
                saveFilterStateToHistory(
                    1,
                    Array.from(selectedCategories),
                    isClosedIncluded,
                    currentSort
                );

         //       const boardCode = document.getElementById("boardCode")?.value;

            }*/

            window.location.href = `/contest/detail/${id}?boardCode=${boardCode}`;
        });
    });
}



function toggleCategory(btn) {
    const id = btn.dataset.id;

    // 전체 버튼 비활성화
    document.querySelector('.category-btn[data-id=""]').classList.remove("active");

    if (selectedCategories.has(id)) {
        selectedCategories.delete(id);
        btn.classList.remove("active");
    } else {
        selectedCategories.add(id);
        btn.classList.add("active");
    }
    fetchFilteredContests({
        page: 1,
        categoryIds: Array.from(selectedCategories),
        sort: currentSort,
        isClosedIncluded
    });
}

function selectAllCategories(btn) {
    document.querySelectorAll(".category-btn").forEach(b => b.classList.remove("active"));
    btn.classList.add("active");
    selectedCategories.clear();
    fetchFilteredContests({
        page: 1,
        categoryIds: Array.from(selectedCategories),
        sort: currentSort,
        isClosedIncluded
    });
}









function changeSort(btn) {
    document.querySelectorAll(".sort-btn").forEach(b => b.classList.remove("active"));
    btn.classList.add("active");

    currentSort = btn.dataset.sort;
    fetchFilteredContests({
        page: 1,
        categoryIds: Array.from(selectedCategories),
        sort: currentSort,
        isClosedIncluded,
        boardCode
    });// 정렬 기준 포함해서 재요청
}

function changeShow() {
    isClosedIncluded =  document.getElementById("isClosedIncluded").checked;
    fetchFilteredContests({
        page: 1,
        categoryIds: Array.from(selectedCategories),
        sort: currentSort,
        isClosedIncluded,
        boardCode
    });
}


function saveFilterStateToHistory(page, categoryIds , isClosedIncluded, sort) {
    const params = new URLSearchParams();

    if (categoryIds.length > 0) {
        params.set("categoryIds", categoryIds.join(","));
    }



    params.set("boardCode", boardCode);
    params.set("sort", sort);
    params.set("isClosedIncluded", isClosedIncluded);
    params.set("page", page);  // 이 변수는 당신 코드에 맞게

    const url = "/contest/list?" + params.toString();


    console.log("디버그요" + categoryIds);
    // pushState로 현재 필터 상태 저장
    history.pushState({
        boardCode,
        categoryIds,
        sort,
        isClosedIncluded,
        page
    }, "", url);

    console.log("디버그요2" + categoryIds);
}



function initializeStateFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);

    const categoryParam = urlParams.get("categoryIds");
    if (categoryParam) {
        categoryParam.split(",").forEach(id => selectedCategories.add(id));
    }

    const sortParam = urlParams.get("sort");
    if (sortParam) currentSort = sortParam;

    const closedParam = urlParams.get("isClosedIncluded");
    if (closedParam === "true") isClosedIncluded = true;

    const pageParam = urlParams.get("page");
    if (pageParam) currentPage = parseInt(pageParam);

    const boardCodeParam = urlParams.get("boardCode");
    if (boardCode) boardCode = boardCodeParam;
}




function restoreContestUI(categoryIds = [], isClosedIncluded = false, sort = "recent") {
    selectedCategories.clear(); // 기존 선택 초기화

    // 카테고리 버튼 상태 초기화
    document.querySelectorAll(".category-btn").forEach(btn => {
        const id = btn.dataset.id;
        if (id && categoryIds.includes(id)) {
            selectedCategories.add(id);
            btn.classList.add("active");
        } else {
            btn.classList.remove("active");
        }
    });

    // '전체' 버튼 처리
    if (categoryIds.length === 0) {
        document.querySelector('.category-btn[data-id=""]').classList.add("active");
    } else {
        document.querySelector('.category-btn[data-id=""]').classList.remove("active");
    }

    // 마감 여부 체크박스 반영
    const closedCheckbox = document.getElementById("isClosedIncluded");
    if (closedCheckbox) closedCheckbox.checked = isClosedIncluded;

    // 정렬 버튼 상태 반영
    document.querySelectorAll(".sort-btn").forEach(btn => {
        btn.classList.toggle("active", btn.dataset.sort === sort);
    });

    // 정렬 상태 변수도 갱신
    currentSort = sort;

}