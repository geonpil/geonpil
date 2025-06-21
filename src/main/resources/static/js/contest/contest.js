// FilterUtils 전역 객체 사용 (import 대신)

// 상태 관리 변수
const selectedCategories = new Set();
let currentSort = "recent";
let isClosedIncluded = false;
let boardCode = "";
let currentPage = 1;

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
            }
        }
    });

    // 반환된 상태에서 값 가져오기
    currentPage = state.currentPage;
    currentSort = state.sort;
    isClosedIncluded = state.isClosedIncluded;

    // 최초 로드 여부 확인 (공통 유틸 사용)
    const initialLoadCheck = FilterUtils.isInitialLoad({
        urlParams,
        additionalChecks: [
            state.sort === "recent",
            state.isClosedIncluded === false
        ]
    });

    // UI 업데이트
    restoreContestUI(Array.from(selectedCategories), isClosedIncluded, currentSort);

    // 최초 로드가 아닌 경우에만 데이터 다시 가져오기
    if (!initialLoadCheck) {
        console.log("파라미터가 있는 로드: 데이터 다시 가져오기");
        fetchFilteredContests({
            page: currentPage,
            categoryIds: Array.from(selectedCategories),
            isClosedIncluded: isClosedIncluded,
            sort: currentSort
        });
    } else {
        console.log("최초 로드: 서버 렌더링 데이터 사용");
        // 이미 서버에서 렌더링된 데이터 사용, 추가 요청하지 않음
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

function fetchFilteredContests({ page = 1, categoryIds = [], isClosedIncluded = false, sort = "recent"}) {
    const params = new URLSearchParams();

    if (categoryIds.length > 0) {
        params.set("categoryIds", categoryIds.join(","));
    }

    params.set("boardCode", boardCode);
    params.set("page", page);
    params.set("sort", sort);
    params.set("isClosedIncluded", isClosedIncluded);

    console.log("필터 파라미터:", params.toString());

    // URL 상태 저장 (공통 유틸 사용)
    FilterUtils.saveFilterStateToHistory({
        page,
        categoryIds,
        baseUrl: "contest/list",
        boardCode,
        additionalParams: {
            sort: sort,
            isClosedIncluded: isClosedIncluded.toString()
        },
        state: {
            sort,
            isClosedIncluded
        }
    });

    // 게시글 목록 로드
    fetch("/contest/list/fragment?" + params.toString())
        .then(res => res.text())
        .then(html => {
            document.getElementById("contest-list").innerHTML = html;
            highlightSelectedButtons();
            bindContestClickEvents();
        })
        .catch(err => console.error("게시글 불러오기 실패", err));

    // 페이지네이션 로드
    fetch("/contest/list/fragment/pagination?" + params.toString())
        .then(res => res.text()
            .then(html => {
                document.getElementById("pagination-area").innerHTML = html;
            }))
        .catch(err => console.error("페이지네이션 불러오기 실패", err));

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
    // 공통 유틸 함수 사용
    FilterUtils.toggleCategoryCommon(btn, selectedCategories, (isAllCategory) => {
        if (isAllCategory) {
            fetchFilteredContests({
                page: 1,
                categoryIds: [],
                sort: currentSort,
                isClosedIncluded
            });
            return;
        }

        fetchFilteredContests({
            page: 1,
            categoryIds: Array.from(selectedCategories),
            sort: currentSort,
            isClosedIncluded
        });
    });
}

// 전체 카테고리 선택 처리
function selectAllCategories(btn) {
    // 공통 유틸 함수 사용
    FilterUtils.selectAllCategoriesCommon(btn, selectedCategories, () => {
        fetchFilteredContests({
            page: 1,
            categoryIds: [],
            sort: currentSort,
            isClosedIncluded
        });
    });
}

// 정렬 방식 변경 처리
function changeSort(btn) {
    document.querySelectorAll(".sort-btn").forEach(b => b.classList.remove("active"));
    btn.classList.add("active");

    currentSort = btn.dataset.sort;
    fetchFilteredContests({
        page: 1,
        categoryIds: Array.from(selectedCategories),
        sort: currentSort,
        isClosedIncluded
    });
}

// 마감된 공모전 표시 여부 변경 처리
function changeShow() {
    isClosedIncluded = document.getElementById("isClosedIncluded").checked;
    fetchFilteredContests({
        page: 1,
        categoryIds: Array.from(selectedCategories),
        sort: currentSort,
        isClosedIncluded
    });
}

// UI 상태 복원
function restoreContestUI(categoryIds = [], isClosedVal = false, sort = "recent") {
    // 카테고리 버튼 상태 업데이트 (공통 유틸 사용)
    FilterUtils.updateCategoryButtonsUI(categoryIds);

    // 마감 여부 체크박스 반영
    const closedCheckbox = document.getElementById("isClosedIncluded");
    if (closedCheckbox) closedCheckbox.checked = isClosedVal;

    // 정렬 버튼 상태 반영
    document.querySelectorAll(".sort-btn").forEach(btn => {
        btn.classList.toggle("active", btn.dataset.sort === sort);
    });
}
