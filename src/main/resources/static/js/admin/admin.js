document.querySelectorAll('.admin-menu').forEach(menu => {
    menu.addEventListener('click', (e) => {
        e.preventDefault();
        const action = menu.dataset.action;
        const section = menu.dataset.section || 'book-picks'; // 기본값 book-picks

        // 섹션별 API 엔드포인트 결정
        let apiUrl;
        if (section === 'categories') {
            apiUrl = `/admin/categories/fragment/${action}`;
        } else {
            apiUrl = `/admin/book-picks/fragment/${action}`;
        }

        csrfFetch(apiUrl)
            .then(res => res.text())
            .then(html => {
                document.getElementById("admin-content").innerHTML = html;
                window.scrollTo({ top: 0, behavior: 'smooth' });

                // 섹션별 초기화 함수 호출
                if (section === 'book-picks') {
                    if(action === 'add'){
                        initSearchBar();
                        initSearchableBookClickHandler();
                        initSubmitBookPick();
                    }else if(action === 'delete'){
                        initDeleteBookPick();
                    }
                } else if (section === 'categories') {
                    if(action === 'add'){
                        initCategoryAdd();
                    }else if(action === 'delete'){
                        initCategoryDelete();
                    }
                }
            })
            .catch(err => {
                alert("페이지를 불러오는 데 실패했습니다.");
                console.error(err);
            });
    });
});

// 메시지 표시 함수
window.showMessage = function(message, type = 'info') {
    const messageDiv = document.getElementById('categoryAddMessage');
    if (!messageDiv) {
        console.warn('categoryAddMessage 요소를 찾을 수 없습니다.');
        return;
    }
    
    messageDiv.textContent = message;
    messageDiv.className = `message ${type}`;
    messageDiv.style.display = 'block';
    
    // 성공 메시지일 때는 5초, 에러는 3초 후 숨기기
    const hideTime = type === 'success' ? 5000 : 3000;
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, hideTime);
}

// 카테고리 삭제용 메시지 표시 함수
window.showDeleteMessage = function(message, type = 'info') {
    const messageDiv = document.getElementById('categoryDeleteMessage');
    if (!messageDiv) {
        console.warn('categoryDeleteMessage 요소를 찾을 수 없습니다.');
        return;
    }
    
    messageDiv.textContent = message;
    messageDiv.className = `message ${type}`;
    messageDiv.style.display = 'block';
    
    // 5초 후 메시지 자동 숨기기
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}

// 카테고리 목록 로드 함수
window.loadCategoriesByBoardCode = function(boardCode) {
    if (!boardCode) {
        const container = document.getElementById('categoryListContainer');
        if (container) container.style.display = 'none';
        return;
    }
    
    // 로딩 상태 표시
    const categoryList = document.getElementById('categoryList');
    const container = document.getElementById('categoryListContainer');
    const noDataMessage = document.getElementById('noCategoriesMessage');
    
    if (!categoryList || !container) {
        console.warn('카테고리 관련 DOM 요소를 찾을 수 없습니다.');
        return;
    }
    
    categoryList.innerHTML = '<div class="loading">카테고리 목록을 불러오는 중...</div>';
    container.style.display = 'block';
    if (noDataMessage) noDataMessage.style.display = 'none';
    
    // API 호출하여 카테고리 목록 가져오기
    csrfFetch(`/admin/categories?boardCode=${boardCode}`)
        .then(response => response.json())
        .then(categories => {
            if (categories.length === 0) {
                categoryList.innerHTML = '';
                if (noDataMessage) noDataMessage.style.display = 'block';
            } else {
                window.renderCategoryList(categories);
                if (noDataMessage) noDataMessage.style.display = 'none';
            }
        })
        .catch(error => {
            console.error('카테고리 목록 로드 실패:', error);
            categoryList.innerHTML = '<div class="error">카테고리 목록을 불러오는데 실패했습니다.</div>';
            window.showDeleteMessage('카테고리 목록을 불러오는데 실패했습니다.', 'error');
        });
}

// 카테고리 목록 렌더링 함수
window.renderCategoryList = function(categories) {
    const categoryList = document.getElementById('categoryList');
    if (!categoryList) return;
    
    categoryList.innerHTML = categories.map(category => `
        <div class="category-item">
            <span class="category-name">${category.categoryName}</span>
            <button type="button" 
                    class="btn btn-danger btn-sm" 
                    onclick="window.deleteCategory(${category.id}, '${category.categoryName}')">
                삭제
            </button>
        </div>
    `).join('');
}

// 카테고리 삭제 함수
window.deleteCategory = function(categoryId, categoryName) {
    if (!confirm(`"${categoryName}" 카테고리를 정말 삭제하시겠습니까?\\n\\n삭제된 카테고리는 복구할 수 없습니다.`)) {
        return;
    }
    
    csrfFetch(`/admin/categories/${categoryId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            window.showDeleteMessage(`"${categoryName}" 카테고리가 삭제되었습니다.`, 'success');
            // 목록 다시 로드
            const boardCode = document.getElementById('deleteBoardCode').value;
            window.loadCategoriesByBoardCode(boardCode);
        } else {
            return response.text().then(errorText => {
                throw new Error(errorText || '삭제에 실패했습니다.');
            });
        }
    })
    .catch(error => {
        console.error('카테고리 삭제 실패:', error);
        window.showDeleteMessage(error.message || '카테고리 삭제에 실패했습니다.', 'error');
    });
}

// 카테고리 추가 폼 초기화
function initCategoryAdd() {
    const form = document.getElementById('categoryAddForm');
    if (form) {
        form.addEventListener('submit', handleCategoryAddSubmit);
    }
}

// 카테고리 삭제 폼 초기화  
function initCategoryDelete() {
    console.log('카테고리 삭제 폼이 초기화되었습니다.');
    
    // 게시판 선택 이벤트 리스너 등록
    const boardSelect = document.getElementById('deleteBoardCode');
    if (boardSelect) {
        boardSelect.addEventListener('change', function() {
            console.log('게시판 선택됨:', this.value);
            window.loadCategoriesByBoardCode(this.value);
        });
    }
}

// 카테고리 추가 폼 제출 처리
function handleCategoryAddSubmit(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const data = {
        boardCode: parseInt(formData.get('boardCode')),
        categoryName: formData.get('categoryName').trim()
    };
    
    // 유효성 검사
    if (!data.boardCode) {
        window.showMessage('게시판을 선택해주세요.', 'error');
        return;
    }
    
    if (!data.categoryName) {
        window.showMessage('카테고리 이름을 입력해주세요.', 'error');
        return;
    }
    
    if (data.categoryName.length > 50) {
        window.showMessage('카테고리 이름은 50자 이하로 입력해주세요.', 'error');
        return;
    }
    
    // API 호출
    csrfFetch('/admin/categories', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            window.showMessage('카테고리가 성공적으로 추가되었습니다.', 'success');
            const form = document.getElementById('categoryAddForm');
            const boardCode = form.elements['boardCode'].value;
            
            // 폼 초기화
            form.reset();
            
            // 추가된 카테고리 목록 새로고침
            if (boardCode && window.showRecentCategories) {
                setTimeout(() => {
                    window.showRecentCategories(parseInt(boardCode));
                }, 500);
            }
        } else {
            return response.text().then(errorText => {
                throw new Error(errorText || '카테고리 추가에 실패했습니다.');
            });
        }
    })
    .catch(error => {
        console.error('카테고리 추가 실패:', error);
        window.showMessage(error.message || '카테고리 추가에 실패했습니다.', 'error');
    });
}