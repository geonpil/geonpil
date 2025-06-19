let selectedCategories = new Set();

function fetchBoardPosts(page = 1,  keyword = "", categoryParam = new Set()) {
    const params = new URLSearchParams();

    highlightSelectedButtons();

    //카테고리 파라미터 세팅
    if (categoryParam.size > 0) {
        params.set("categoryIds", Array.from(categoryParam).join(","));
    }
    //게시판 코드 파라미터 세팅
    const boardCode = new URLSearchParams(window.location.search).get("boardCode");
    if (boardCode) params.set("boardCode", boardCode);

    //페이지 파라미터 세팅
    params.set("page", page);

    let url ="";

   if (keyword) {
        // 검색용
        params.set("keyword", keyword);
        url = "/api/search/board?" + params.toString();
    } else {
        // 일반 목록용
        url = "/board/list/fragment?" + params.toString();
      //  urlForPage = "/board/list/fragment/pagination?" + params.toString();
   }

    history.pushState({}, '', "/board/list?" + params.toString());

    fetch(url)
        .then(res => res.text())
        .then(html => {
            document.getElementById("post-list").innerHTML = html;
        });



}

//뒤로 가기시
window.addEventListener("popstate", () => {
    
    console.log("popstate 발생");
    
    const urlParams = new URLSearchParams(window.location.search);
    const page = parseInt(urlParams.get("page")) || 1;

    // ✅ 선택된 카테고리 복원 (선택사항)
    const categoryIdString = urlParams.get("categoryIds") || "";
    selectedCategories = new Set(categoryIdString.split(",").filter(Boolean));
    highlightSelectedButtons(); // UI 반영 함수

    fetchBoardPosts(page, "",selectedCategories);
});




function restoreBoardUI(categoryIds = [], page = 1) {
    // 모든 카테고리 버튼 초기화
    document.querySelectorAll(".category-btn").forEach(btn => btn.classList.remove("active"));

    // 카테고리가 없거나 비어있으면 '전체' 카테고리 활성화
    if (!categoryIds || categoryIds.length === 0) {
        initSelectAllCategory();
        return;
    }

    // 카테고리가 있는 경우, 해당 카테고리 버튼 활성화
    categoryIds.forEach(id => {
        const btn = document.querySelector(`.category-btn[data-id="${id}"]`);
        if (btn) btn.classList.add("active");
    });

    // 검색창 값 복원 (URL에 keyword가 있는 경우)
    const urlParams = new URLSearchParams(window.location.search);
    const keyword = urlParams.get("keyword");
    if (keyword) {
        const searchInput = document.getElementById("search-input");
        if (searchInput) searchInput.value = keyword;
    }

    console.log("UI가 복원되었습니다. 페이지:", page, "카테고리:", categoryIds);
}

function initSelectAllCategory(){
    // ✅ '전체' 버튼에 .active 클래스 부여
    const allBtn = document.querySelector('.category-btn[data-id=""]');
    if (allBtn) allBtn.classList.add("active");
}




document.querySelectorAll(".category-btn").forEach(btn => {
    btn.addEventListener("click", () => {
        const keyword = document.getElementById("search-input").value.trim()||"";

        if (btn.dataset.id === "") {
            selectAllCategories(btn, keyword);
        } else {
            toggleCategory(btn, keyword);
        }
    });
})

function toggleCategory(btn, keyword) {
    const id = btn.dataset.id;

    document.querySelector('.category-btn[data-id=""]').classList.remove("active");

    if (selectedCategories.has(id)) {
        selectedCategories.delete(id);

        // 선택된 카테고리가 하나도 없으면 '전체' 카테고리를 활성화
        if (selectedCategories.size === 0) {
            document.querySelector('.category-btn[data-id=""]').classList.add("active");
            // 전체 카테고리일 때는 categoryIds 파라미터를 명시적으로 빈 값으로 전달
            fetchBoardPosts(1, keyword, new Set());
            return; // 여기서 함수 종료
        }
    } else {
        selectedCategories.add(id);
    }
    highlightSelectedButtons();

    fetchBoardPosts(1, keyword, selectedCategories);
}


function selectAllCategories(btn, keyword) {
    // 모든 카테고리 버튼 비활성화
    document.querySelectorAll(".category-btn").forEach(b => b.classList.remove("active"));

    // 전체 버튼만 활성화
    btn.classList.add("active");

    // 선택된 카테고리 Set 비우기
    selectedCategories.clear();

    // 전체 게시글 요청
    fetchBoardPosts(1, keyword, selectedCategories);
}


addEventListener("DOMContentLoaded", () => {
    console.log("DOMContentLoaded 이벤트 발생");

    const urlParams = new URLSearchParams(window.location.search);
    const page = parseInt(urlParams.get("page") || 1);
    const categoryIds = urlParams.get("categoryIds") || "";
    const keyword = urlParams.get("keyword") || "";

    // 디버깅을 위한 로그 추가
    console.log("초기 파라미터 확인:", { page, categoryIds, keyword });

    // 최초 로딩 판단 - categoryIds가 undefined나 빈 문자열일 경우 포함
    const isInitialLoad = !keyword && (!categoryIds || categoryIds === "") && page === 1;

    console.log("초기 로드 여부:", isInitialLoad);

    if (isInitialLoad) {
        console.log("최초 로드: 전체 카테고리 활성화");
        initSelectAllCategory();
    } else {
        console.log("파라미터 있는 로드: UI 복원");
        // 페이지가 로드될 때 카테고리와 페이지를 복원
        const categorySet = categoryIds ? new Set(categoryIds.split(",").filter(Boolean)) : new Set();
        selectedCategories = categorySet; // 선택된 카테고리 복원
        fetchBoardPosts(page, keyword, selectedCategories);
        restoreBoardUI(Array.from(categorySet), page);
    }
});
