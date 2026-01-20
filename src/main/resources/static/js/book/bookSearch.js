function fetchBookSearchResults(page, query = 1) {

    const encodedQuery = encodeURIComponent(query);
    const mode = document.body.dataset.mode;

    if (window.location.pathname === '/') {
        // 👉 메인화면일 경우 → 전체 이동
        window.location.href = `/api/search?query=${encodedQuery}&page=1`;
    } else {
        // 👉 검색결과 화면일 경우 → AJAX로 내용만 갱신
        const newUrl = `/api/search?query=${encodedQuery}&page=${page}`;


        if(mode === 'review'){
            history.pushState({}, '', newUrl);
        }

        //document.querySelector(".search-header h2").textContent = "검색어: " + query;


        csrfFetch(`/api/search/fragment/result?query=${encodeURIComponent(query)}&page=${page}&mode=${mode}`)
            .then(res => res.text())
            .then(html => {
                document.querySelector("#book-list-area").innerHTML = html;
                window.scrollTo({top: 0, behavior: 'smooth'});
            });

        csrfFetch(`/api/search/fragment/pagination?query=${encodeURIComponent(query)}&page=${page}`)
            .then(res => res.text())
            .then(html => {
                document.querySelector("#pagination-area").innerHTML = html;
                window.scrollTo({top: 0, behavior: 'smooth'});
            });
    }
}

let autocompleteTimeout = null;
let selectedSuggestionIndex = -1;

function initSearchBar() {
    const searchInput = document.getElementById("searchInput");
    const searchButton = document.querySelector(".btn-primary");
    const autocompleteDropdown = document.getElementById("autocompleteDropdown");

    if (!searchInput) return;

    // 검색 버튼 클릭 이벤트
    if (searchButton) {
        searchButton.addEventListener("click", () => {
            const query = searchInput.value.trim();
            if (!query) {
                alert("검색어를 입력해주세요.");
                return;
            }
            hideAutocomplete();
            fetchBookSearchResults(1, query);
        });
    }

    // Enter 키 이벤트
    searchInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            const query = searchInput.value.trim();
            if (!query) {
                alert("검색어를 입력해주세요.");
                return;
            }
            hideAutocomplete();
            fetchBookSearchResults(1, query);
        } else if (e.key === "ArrowDown") {
            e.preventDefault();
            navigateSuggestions(1);
        } else if (e.key === "ArrowUp") {
            e.preventDefault();
            navigateSuggestions(-1);
        } else if (e.key === "Escape") {
            hideAutocomplete();
        }
    });

    // 입력 이벤트 - 자동완성 제안 가져오기
    searchInput.addEventListener("input", (e) => {
        const query = e.target.value.trim();
        selectedSuggestionIndex = -1;

        // 이전 타이머 취소
        if (autocompleteTimeout) {
            clearTimeout(autocompleteTimeout);
        }

        if (query.length < 2) {
            hideAutocomplete();
            return;
        }

        // 디바운싱: 300ms 후에 자동완성 요청
        autocompleteTimeout = setTimeout(() => {
            fetchSearchSuggestions(query);
        }, 300);
    });

    // 외부 클릭 시 자동완성 숨기기
    document.addEventListener("click", (e) => {
        const searchInputWrapper = searchInput.closest(".search-input-wrapper");
        if (searchInputWrapper && !searchInputWrapper.contains(e.target)) {
            hideAutocomplete();
        }
    });

    // 포커스 시 자동완성 다시 표시 (입력값이 있으면)
    searchInput.addEventListener("focus", () => {
        const query = searchInput.value.trim();
        if (query.length >= 2) {
            fetchSearchSuggestions(query);
        }
    });
}

function fetchSearchSuggestions(query) {
    if (!query || query.length < 2) {
        hideAutocomplete();
        return;
    }

    csrfFetch(`/api/search/suggestions?q=${encodeURIComponent(query)}&limit=10`)
        .then(response => response.json())
        .then(suggestions => {
            if (suggestions && suggestions.length > 0) {
                displaySuggestions(suggestions, query);
            } else {
                hideAutocomplete();
            }
        })
        .catch(error => {
            console.error("자동완성 조회 실패:", error);
            hideAutocomplete();
        });
}

function displaySuggestions(suggestions, query) {
    const autocompleteDropdown = document.getElementById("autocompleteDropdown");
    if (!autocompleteDropdown) return;

    // 검색어를 강조 표시하기 위한 함수
    const highlightQuery = (text, query) => {
        const regex = new RegExp(`(${query})`, "gi");
        return text.replace(regex, "<strong>$1</strong>");
    };

    autocompleteDropdown.innerHTML = suggestions
        .map((suggestion, index) => {
            const highlighted = highlightQuery(suggestion, query);
            return `
                <div class="autocomplete-item ${index === selectedSuggestionIndex ? 'selected' : ''}" 
                     data-index="${index}" 
                     data-value="${suggestion.replace(/"/g, '&quot;')}">
                    ${highlighted}
                </div>
            `;
        })
        .join("");

    // 각 제안 항목에 클릭 이벤트 추가
    autocompleteDropdown.querySelectorAll(".autocomplete-item").forEach((item, index) => {
        item.addEventListener("click", () => {
            selectSuggestion(item.dataset.value);
        });

        item.addEventListener("mouseenter", () => {
            selectedSuggestionIndex = index;
            updateSelectedItem();
        });
    });

    autocompleteDropdown.style.display = "block";
}

function navigateSuggestions(direction) {
    const autocompleteDropdown = document.getElementById("autocompleteDropdown");
    if (!autocompleteDropdown || autocompleteDropdown.style.display === "none") {
        return;
    }

    const items = autocompleteDropdown.querySelectorAll(".autocomplete-item");
    if (items.length === 0) return;

    selectedSuggestionIndex += direction;

    if (selectedSuggestionIndex < 0) {
        selectedSuggestionIndex = items.length - 1;
    } else if (selectedSuggestionIndex >= items.length) {
        selectedSuggestionIndex = 0;
    }

    updateSelectedItem();
}

function updateSelectedItem() {
    const autocompleteDropdown = document.getElementById("autocompleteDropdown");
    if (!autocompleteDropdown) return;

    const items = autocompleteDropdown.querySelectorAll(".autocomplete-item");
    items.forEach((item, index) => {
        if (index === selectedSuggestionIndex) {
            item.classList.add("selected");
            item.scrollIntoView({ block: "nearest" });
        } else {
            item.classList.remove("selected");
        }
    });
}

function selectSuggestion(value) {
    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        searchInput.value = value;
        hideAutocomplete();
        fetchBookSearchResults(1, value);
    }
}

function hideAutocomplete() {
    const autocompleteDropdown = document.getElementById("autocompleteDropdown");
    if (autocompleteDropdown) {
        autocompleteDropdown.style.display = "none";
        autocompleteDropdown.innerHTML = "";
    }
    selectedSuggestionIndex = -1;
}

window.addEventListener("DOMContentLoaded", initSearchBar);