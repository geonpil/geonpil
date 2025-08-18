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

// 카테고리 추가 폼 초기화
function initCategoryAdd() {
    const form = document.getElementById('categoryAddForm');
    if (form) {
        form.addEventListener('submit', handleCategoryAddSubmit);
    }
}

// 카테고리 삭제 폼 초기화  
function initCategoryDelete() {
    // _category-delete-fragment.html에서 이미 이벤트 리스너가 설정되므로 
    // 여기서는 추가 설정이 필요하면 작성
    console.log('카테고리 삭제 폼이 초기화되었습니다.');
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