// FilterUtils 전역 객체 사용 (import 대신)

// 상태 관리 변수
const selectedCategories = new Set();
let currentSort = "recent";
let isClosedIncluded = false;
let boardCode = "";
let currentPage = 1;
let keyword = "";
let searchType = "";

// 페이지 초기 로드 시 실행
document.addEventListener("DOMContentLoaded", function () {
    console.log("DOMContentLoaded 이벤트 발생");

    boardCode = document.getElementById("boardCode").value;
    isClosedIncluded = document.getElementById("isClosedIncluded").checked;

    const urlParams = new URLSearchParams(window.location.search);

    // URL에서 상태 복원 (공통 유틸 사용)
    const state = FilterUtils.initializeStateFromUrl({
        selectedCategories,
        additionalParams: {
            sort: { paramName: "sort", defaultValue: "recent" },
            isClosedIncluded: {
                paramName: "isClosedIncluded",
                defaultValue: false,
                transform: value => value === "true"
            },
            keyword: { paramName: "keyword", defaultValue: "" },
            searchType: { paramName: "searchType", defaultValue: "" }
        }
    });

    // 반환된 상태에서 값 가져오기
    currentPage = state.currentPage;
    currentSort = state.sort;
    isClosedIncluded = state.isClosedIncluded;
    keyword = state.keyword;
    searchType = state.searchType;

    // 최초 로드 여부 확인 (공통 유틸 사용)
    const initialLoadCheck = FilterUtils.isInitialLoad({
        urlParams,
        additionalChecks: [
            state.sort === "recent",
            state.isClosedIncluded === false
        ]
    });



    // 최초 로드가 아닌 경우에만 데이터 다시 가져오기
    if (!initialLoadCheck) {
        console.log("파라미터가 있는 로드: 데이터 다시 가져오기");
        fetchFilteredContests({
            page: currentPage,
            categoryIds: Array.from(selectedCategories),
            isClosedIncluded: isClosedIncluded,
            sort: currentSort
        });
        // UI 업데이트
        restoreContestUI(Array.from(selectedCategories), isClosedIncluded, currentSort,keyword, searchType);
    } else {
        console.log("최초 로드: 서버 렌더링 데이터 사용");
        // 이미 서버에서 렌더링된 데이터 사용, 추가 요청하지 않음
        FilterUtils.initSelectAllCategory("contest");
        bindContestClickEvents(); // 이벤트 바인딩만 수행
    }
});

// 뒤로가기/앞으로가기 이벤트 처리
window.addEventListener("popstate", function(event) {
    console.log("popstate 이벤트 발생", event.state);

    if (event.state) {
        // history.state에서 저장된 상태 복원
        boardCode = event.state.boardCode || boardCode;

        // 카테고리 상태 복원
        selectedCategories.clear();
        if (event.state.categoryIds && event.state.categoryIds.length > 0) {
            event.state.categoryIds.forEach(id => selectedCategories.add(id));
        }

        // 정렬 및 마감 포함 여부 상태 복원
        currentSort = event.state.sort || "recent";
        isClosedIncluded = event.state.isClosedIncluded || false;
        currentPage = event.state.page || 1;

        // UI 복원
        restoreContestUI(Array.from(selectedCategories), isClosedIncluded, currentSort);

        // 데이터 다시 로드
        fetchFilteredContests({
            page: currentPage,
            categoryIds: Array.from(selectedCategories),
            isClosedIncluded: isClosedIncluded,
            sort: currentSort
        });
    } else {
        // state가 없는 경우 기본값으로 초기화
        const state = FilterUtils.initializeStateFromUrl({
            selectedCategories,
            additionalParams: {
                sort: { paramName: "sort", defaultValue: "recent" },
                isClosedIncluded: {
                    paramName: "isClosedIncluded",
                    defaultValue: false,
                    transform: value => value === "true"
                }
            }
        });

        // 반환된 상태에서 값 가져오기
        currentPage = state.currentPage;
        currentSort = state.sort;
        isClosedIncluded = state.isClosedIncluded;

        restoreContestUI(Array.from(selectedCategories), isClosedIncluded, currentSort);
        fetchFilteredContests({
            page: currentPage,
            categoryIds: Array.from(selectedCategories),
            isClosedIncluded: isClosedIncluded,
            sort: currentSort
        });
    }
});

function fetchFilteredContests({ page = 1, categoryIds = [], isClosedIncluded = false, sort = "recent", keyword = "", searchType = ""}) {
    const params = new URLSearchParams();

    if (categoryIds.length > 0) {
        params.set("categoryIds", categoryIds.join(","));
    }

    params.set("boardCode", boardCode);
    params.set("page", page);
    params.set("sort", sort);
    params.set("isClosedIncluded", isClosedIncluded);
    params.set("keyword", keyword);
    params.set("searchType", searchType);

    console.log("필터 파라미터:", params.toString());

    // URL 상태 저장 (공통 유틸 사용)
    FilterUtils.saveFilterStateToHistory({
        page,
        categoryIds,
        baseUrl: "contest/list",
        boardCode,
        additionalParams: {
            sort: sort,
            isClosedIncluded: isClosedIncluded.toString(),
            keyword: keyword,
            searchType : searchType
        },
        state: {
            sort,
            isClosedIncluded,
            keyword,
            searchType
        }
    });

    // 통합된 엔드포인트로 요청하여 게시글 목록과 페이지네이션을 한 번에 가져옴
    fetch("/api/search/contest?" + params.toString())
        .then(res => res.text())
        .then(html => {
            // 응답에 목록과 페이지네이션이 모두 포함되어 있으므로
            // 최상위 컨테이너에 HTML을 삽입
            const container = document.querySelector(".board-container");

            // 기존 목록과 페이지네이션 영역을 찾음
            const contestListArea = document.getElementById("contest-list");
            const paginationArea = document.getElementById("pagination-area");

            // 임시 요소를 생성하여 응답 HTML을 파싱
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;

            // 응답에서 목록과 페이지네이션 부분을 추출
            const newContestList = tempDiv.querySelector("#contest-list");
            const newPaginationArea = tempDiv.querySelector("#pagination-area");

            // 기존 요소 업데이트
            if (newContestList && contestListArea) {
                contestListArea.innerHTML = newContestList.innerHTML;
            }

            if (newPaginationArea && paginationArea) {
                paginationArea.innerHTML = newPaginationArea.innerHTML;
            }

            // UI 상태 업데이트 및 이벤트 핸들러 재바인딩
            highlightSelectedButtons();
            bindContestClickEvents();
        })
        .catch(err => console.error("공모전 데이터 로드 실패", err));

    // 현재 페이지 업데이트
    currentPage = page;
}

// 카테고리 버튼 강조 표시
function highlightSelectedButtons() {
    // 카테고리 버튼 업데이트 (공통 유틸 사용)
    FilterUtils.updateCategoryButtonsUI(selectedCategories);

    // 정렬 버튼 강조
    document.querySelectorAll(".sort-btn").forEach(btn => {
        btn.classList.toggle("active", btn.dataset.sort === currentSort);
    });

    // 마감 포함 체크박스 상태 설정
    const closedCheckbox = document.getElementById("isClosedIncluded");
    if (closedCheckbox) {
        closedCheckbox.checked = isClosedIncluded;
    }
}

// 게시물 클릭 이벤트 바인딩
function bindContestClickEvents() {
    document.querySelectorAll(".contest-row").forEach(row => {
        row.addEventListener("click", function () {
            const id = this.dataset.id;
            if (id) {
                window.location.href = `/contest/detail/${id}?boardCode=${boardCode}`;
            }
        });
    });
}

// 카테고리 토글 처리
function toggleCategory(btn) {
    settingKeywordAndSearchType();
    // 공통 유틸 함수 사용
    FilterUtils.toggleCategoryCommon(btn, selectedCategories, (isAllCategory) => {
        if (isAllCategory) {
            fetchFilteredContests({
                page: 1,
                categoryIds: [],
                sort: currentSort,
                isClosedIncluded,
                keyword,
                searchType
            });
            return;
        }

        fetchFilteredContests({
            page: 1,
            categoryIds: Array.from(selectedCategories),
            sort: currentSort,
            isClosedIncluded,
            keyword,
            searchType
        });
    });
}

// 전체 카테고리 선택 처리
function selectAllCategories(btn) {
    settingKeywordAndSearchType();

    // 공통 유틸 함수 사용
    FilterUtils.selectAllCategoriesCommon(btn, selectedCategories, () => {
        fetchFilteredContests({
            page: 1,
            categoryIds: [],
            sort: currentSort,
            isClosedIncluded,
            keyword,
            searchType
        });
    });
}

// 정렬 방식 변경 처리
function changeSort(btn) {
    document.querySelectorAll(".sort-btn").forEach(b => b.classList.remove("active"));
    btn.classList.add("active");

    settingKeywordAndSearchType();

    currentSort = btn.dataset.sort;
    fetchFilteredContests({
        page: 1,
        categoryIds: Array.from(selectedCategories),
        sort: currentSort,
        isClosedIncluded,
        keyword,
        searchType
    });
}

// 마감된 공모전 표시 여부 변경 처리
function changeShow() {
    isClosedIncluded = document.getElementById("isClosedIncluded").checked;
    settingKeywordAndSearchType();
    fetchFilteredContests({
        page: 1,
        categoryIds: Array.from(selectedCategories),
        sort: currentSort,
        isClosedIncluded,
        keyword,
        searchType
    });
}

// UI 상태 복원
function restoreContestUI(categoryIds = [], isClosedVal = false, sort = "recent",keyword = "", searchType = "") {
    // 카테고리 버튼 상태 업데이트 (공통 유틸 사용)
    FilterUtils.updateCategoryButtonsUI(categoryIds);


    // 마감 여부 체크박스 반영
    const closedCheckbox = document.getElementById("isClosedIncluded");
    if (closedCheckbox) closedCheckbox.checked = isClosedVal;

    // 정렬 버튼 상태 반영
    document.querySelectorAll(".sort-btn").forEach(btn => {
        btn.classList.toggle("active", btn.dataset.sort === sort);
    });

    const searchInput = document.getElementById("search-input");
    searchInput.value = keyword;
    const searchTypeSelect = document.getElementById("searchType");
    if (searchTypeSelect) {
        searchTypeSelect.value = searchType;
    }
}

function settingKeywordAndSearchType() {
    keyword = document.getElementById("search-input").value.trim() || "";
    searchType = document.getElementById("searchType").value;
}
