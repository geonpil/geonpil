// FilterUtils 전역 객체 사용 (import 대신)

// 상태 관리 변수
let selectedCategories = new Set();
let currentPage = 1;
let boardCode = "";

function fetchBoardPosts(page = 1, keyword = "", categoryParam = new Set(), searchType = "") {
    const params = new URLSearchParams();

    // 화면 버튼 상태 업데이트
    highlightSelectedButtons();

    // 카테고리 파라미터 세팅
    if (categoryParam.size > 0) {
        params.set("categoryIds", Array.from(categoryParam).join(","));
    }

    // 게시판 코드 파라미터 세팅
    boardCode = new URLSearchParams(window.location.search).get("boardCode") || boardCode;
    if (boardCode) params.set("boardCode", boardCode);

    // 페이지 파라미터 세팅
    params.set("page", page);

    // 현재 페이지 저장
    currentPage = page;

    let url = "";
    let additionalParams = {};

// 검색어가 있는 경우에만 키워드 파라미터 추가
    if (keyword) {
        params.set("keyword", keyword);
        params.set("searchType", searchType); // 기본 검색 타입은 제목
        additionalParams.keyword = keyword;
        additionalParams.searchType = searchType;
    }

// 통합 API 호출 (검색어 유무와 관계없이 동일 엔드포인트 사용)
    url = "/api/search/board?" + params.toString();

    // URL 상태 저장 (공통 유틸 사용)
    FilterUtils.saveFilterStateToHistory({
        page,
        categoryIds: Array.from(categoryParam),
        baseUrl: "board/list",
        boardCode,
        additionalParams,
        state: { keyword }
    });

    // 게시글 목록 로드
    fetch(url)
        .then(res => res.text())
        .then(html => {
            document.getElementById("post-list").innerHTML = html;
        });
}

// 뒤로 가기 시 (공통 유틸 사용)
window.addEventListener("popstate", () => {
    console.log("popstate 발생");
    
    // URL에서 상태 복원
    const urlParams = new URLSearchParams(window.location.search);
    const state = FilterUtils.initializeStateFromUrl({
        selectedCategories,
        additionalParams: {
            keyword: { paramName: "keyword", defaultValue: "" },
            searchType: { paramName: "searchType", defaultValue: "" }
        }
    });

    // 반환된 상태에서 값 가져오기
    currentPage = state.currentPage;
    boardCode = state.boardCode;
    const keyword = state.keyword;
    const searchType = state.searchType;

    // UI 반영
    restoreBoardUI(Array.from(selectedCategories), currentPage);

    // 데이터 다시 로드
    fetchBoardPosts(currentPage, keyword, selectedCategories, searchType);
});

function restoreBoardUI(categoryIds = [], page = 1) {
    // 카테고리 버튼 업데이트 (공통 유틸 사용)
    FilterUtils.updateCategoryButtonsUI(categoryIds);

    // 검색창 값 복원 (URL에 keyword가 있는 경우)
    const urlParams = new URLSearchParams(window.location.search);
    const keyword = urlParams.get("keyword");
    if (keyword) {
        const searchInput = document.getElementById("search-input");
        if (searchInput) searchInput.value = keyword;
    }

    console.log("UI가 복원되었습니다. 페이지:", page, "카테고리:", categoryIds);
}

function highlightSelectedButtons() {
    // 카테고리 버튼 업데이트 (공통 유틸 사용)
    FilterUtils.updateCategoryButtonsUI(selectedCategories);
}



// 카테고리 버튼 이벤트 리스너 설정
document.querySelectorAll(".category-btn").forEach(btn => {
    btn.addEventListener("click", () => {
        const keyword = document.getElementById("search-input").value.trim() || "";
        const searchType = document.getElementById("searchType").value.trim() || "";

        if (btn.dataset.id === "") {
            selectAllCategories(btn, keyword, searchType);
        } else {
            toggleCategory(btn, keyword, searchType);
        }
    });
});

// 카테고리 토글 처리
function toggleCategory(btn, keyword, searchType) {
    // 공통 유틸 함수 사용
    FilterUtils.toggleCategoryCommon(btn, selectedCategories, (isAllCategory) => {
        if (isAllCategory) {
            fetchBoardPosts(1, keyword, new Set(), searchType);
            return;
        }

        fetchBoardPosts(1, keyword, selectedCategories, searchType);
    });
}

// 전체 카테고리 선택 처리
function selectAllCategories(btn, keyword, searchType) {
    // 공통 유틸 함수 사용
    FilterUtils.selectAllCategoriesCommon(btn, selectedCategories, () => {
        fetchBoardPosts(1, keyword, new Set(), searchType);
    });
}

// 페이지 로드 시 초기화
addEventListener("DOMContentLoaded", () => {
    console.log("DOMContentLoaded 이벤트 발생");

    const urlParams = new URLSearchParams(window.location.search);

    // URL에서 상태 복원 (공통 유틸 사용)
    const state = FilterUtils.initializeStateFromUrl({
        selectedCategories,
        additionalParams: {
            keyword: { paramName: "keyword", defaultValue: "" },
            searchType: { paramName: "searchType", defaultValue: "" }
        }
    });

    // 반환된 상태에서 값 가져오기
    currentPage = state.currentPage;
    boardCode = state.boardCode;
    const keyword = state.keyword;
    const searchType = state.searchType;

    // 최초 로드 여부 확인 (공통 유틸 사용)
    const initialLoadCheck = FilterUtils.isInitialLoad({
        urlParams,
        additionalChecks: [
            !keyword || keyword === ""
        ]
    });

    console.log("초기 로드 여부:", initialLoadCheck);

    if (initialLoadCheck) {
        console.log("최초 로드: 전체 카테고리 활성화");
        FilterUtils.initSelectAllCategory();
    } else {
        console.log("파라미터 있는 로드: UI 복원");
        fetchBoardPosts(currentPage, keyword, selectedCategories,searchType);
        restoreBoardUI(Array.from(selectedCategories), currentPage);
    }
});
