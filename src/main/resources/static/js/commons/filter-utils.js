/**
 * 게시판과 공모전 페이지에서 공통으로 사용되는 필터 관련 유틸리티 함수들
 */

// 전역 객체로 내보내기 위한 namespace 생성
const FilterUtils = {};

/**
 * URL 파라미터에서 카테고리와 페이지 정보를 파싱하여 반환
 * @param {Object} options - 옵션 객체
 * @param {Set} options.selectedCategories - 선택된 카테고리 ID 집합
 * @param {Object} options.additionalParams - 페이지별 추가 파라미터 정의 (선택사항)
 * @returns {Object} 파싱된 파라미터 정보
 */
FilterUtils.initializeStateFromUrl = function(options) {
    const { selectedCategories, additionalParams = {} } = options;
    const urlParams = new URLSearchParams(window.location.search);

    // 카테고리 파라미터 처리
    selectedCategories.clear();
    const categoryParam = urlParams.get("categoryIds");
    if (categoryParam) {
        categoryParam.split(",").filter(Boolean).forEach(id => selectedCategories.add(id));
    }

    // 페이지 파라미터 처리
    let currentPage = 1;
    const pageParam = urlParams.get("page");
    if (pageParam) currentPage = parseInt(pageParam);

    // 보드코드 파라미터 처리
    let boardCode = "";
    const boardCodeParam = urlParams.get("boardCode");
    if (boardCodeParam) boardCode = boardCodeParam;

    // 추가 파라미터 처리 (페이지별 특화 파라미터)
    const result = {
        currentPage,
        boardCode,
        selectedCategories
    };

    // 추가 파라미터 처리
    if (additionalParams) {
        Object.entries(additionalParams).forEach(([key, config]) => {
            const param = urlParams.get(config.paramName);
            if (param !== null) {
                result[key] = config.transform ? config.transform(param) : param;
            } else {
                result[key] = config.defaultValue;
            }
        });
    }

    return result;
};

/**
 * 페이지 최초 로드 여부 확인 (파라미터가 없는 경우 최초 로드로 간주)
 * @param {Object} options - 옵션 객체
 * @param {URLSearchParams} options.urlParams - URL 파라미터
 * @param {Array} [options.additionalChecks=[]] - 추가 검사 조건 배열 (모두 true여야 최초 로드로 판단)
 * @returns {boolean} 최초 로드 여부
 */
FilterUtils.isInitialLoad = function(options) {
    const { urlParams, additionalChecks = [] } = options;
    const page = parseInt(urlParams.get("page") || "1");
    const categoryIds = urlParams.get("categoryIds");

    // 기본 조건: 페이지=1이고 카테고리가 없음
    const baseCondition = page === 1 && !categoryIds;

    // 추가 조건이 없으면 기본 조건만으로 판단
    if (additionalChecks.length === 0) {
        return baseCondition;
    }

    // 모든 추가 조건이 참이어야 최초 로드
    return baseCondition && additionalChecks.every(check => check === true);
};

/**
 * 상태 정보를 history API에 저장
 * @param {Object} options - 옵션 객체
 * @param {number} options.page - 페이지 번호
 * @param {Array} options.categoryIds - 선택된 카테고리 ID 배열
 * @param {string} options.baseUrl - 기본 URL 경로 (ex: "board/list" 또는 "contest/list")
 * @param {string} options.boardCode - 게시판 코드
 * @param {Object} [options.additionalParams={}] - 추가 파라미터 객체
 * @param {Object} [options.state={}] - history.state에 저장할 상태 객체
 */
FilterUtils.saveFilterStateToHistory = function(options) {
    const {
        page,
        categoryIds,
        baseUrl,
        boardCode,
        additionalParams = {},
        state = {}
    } = options;

    const params = new URLSearchParams();

    // 카테고리 파라미터
    if (categoryIds && categoryIds.length > 0) {
        params.set("categoryIds", categoryIds.join(","));
    }

    // 기본 파라미터
    params.set("page", page);
    if (boardCode) params.set("boardCode", boardCode);

    // 추가 파라미터
    Object.entries(additionalParams).forEach(([key, value]) => {
        if (value !== null && value !== undefined) {
            params.set(key, value);
        }
    });

    const url = `/${baseUrl}?` + params.toString();

    // 상태 정보 준비
    const historyState = {
        page,
        categoryIds,
        boardCode,
        ...state
    };

    // 현재 상태 저장
    history.pushState(historyState, "", url);
};

/**
 * 카테고리 버튼의 활성 상태를 설정
 * @param {Set|Array} selectedCategoryIds - 선택된 카테고리 ID 세트 또는 배열
 */
FilterUtils.updateCategoryButtonsUI = function(selectedCategoryIds) {
    // Set 타입 변환
    const categorySet = selectedCategoryIds instanceof Set
        ? selectedCategoryIds
        : new Set(selectedCategoryIds);

    // 모든 카테고리 버튼 초기화
    document.querySelectorAll(".category-btn").forEach(btn => {
        const id = btn.dataset.id;
        if (id === "") {
            // 전체 버튼
            btn.classList.toggle("active", categorySet.size === 0);
        } else {
            // 개별 카테고리 버튼
            btn.classList.toggle("active", categorySet.has(id));
        }
    });
};

/**
 * 클릭 이벤트 핸들러를 설정
 * @param {string} selector - 클릭 이벤트를 등록할 요소 선택자
 * @param {Function} handler - 클릭 이벤트 핸들러 함수
 */
FilterUtils.bindClickEvents = function(selector, handler) {
    document.querySelectorAll(selector).forEach(element => {
        element.addEventListener("click", function() {
            handler(this);
        });
    });
};

/**
 * 개별 카테고리 토글 처리 공통 함수
 * @param {Element} btn - 클릭된 버튼 요소
 * @param {Set} selectedCategories - 선택된 카테고리 세트
 * @param {Function} callback - 카테고리 변경 후 실행할 콜백 함수
 */
FilterUtils.toggleCategoryCommon = function(btn, selectedCategories, callback) {
    const id = btn.dataset.id;

    // 전체 버튼 비활성화
    document.querySelector('.category-btn[data-id=""]').classList.remove("active");

    if (selectedCategories.has(id)) {
        selectedCategories.delete(id);

        // 선택된 카테고리가 하나도 없으면 '전체' 카테고리를 활성화
        if (selectedCategories.size === 0) {
            document.querySelector('.category-btn[data-id=""]').classList.add("active");
            callback(true); // true: 전체 카테고리로 변경됨을 알림
            return;
        }
    } else {
        selectedCategories.add(id);
    }

    callback(false); // false: 일반 카테고리 변경
};

/**
 * 전체 카테고리 선택 처리 공통 함수
 * @param {Element} btn - 클릭된 버튼 요소
 * @param {Set} selectedCategories - 선택된 카테고리 세트
 * @param {Function} callback - 카테고리 변경 후 실행할 콜백 함수
 */
FilterUtils.selectAllCategoriesCommon = function(btn, selectedCategories, callback) {
    // 모든 카테고리 버튼 비활성화
    document.querySelectorAll(".category-btn").forEach(b => b.classList.remove("active"));

    // 전체 버튼만 활성화
    btn.classList.add("active");

    // 선택된 카테고리 Set 비우기
    selectedCategories.clear();

    // 콜백 실행
    callback();
};

// 전역 객체에 FilterUtils 내보내기
window.FilterUtils = FilterUtils;
