document.querySelectorAll('.admin-menu').forEach(menu => {
    menu.addEventListener('click', (e) => {
        e.preventDefault();
        const action = menu.dataset.action;
        const section = menu.dataset.section || 'book-picks'; // 기본값 book-picks

        // 섹션별 API 엔드포인트 결정
        let apiUrl;
        if (section === 'categories') {
            apiUrl = `/admin/categories/fragment/${action}`;
        } else if (section === 'notice') {
            apiUrl = `/admin/notice/fragment/${action}`;
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
                } else if (section === 'notice') {
                    if(action === 'add'){
                        // 공지 작성 페이지 초기화
                        setTimeout(() => {
                            initNoticeAdd();
                        }, 100);
                    }else if(action === 'list'){
                        initNoticeList();
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

// 공지 작성 초기화 함수
let noticeEditor = null;

function initNoticeAdd() {
    // Toast UI Editor 라이브러리 로드 확인
    if (typeof toastui === 'undefined' || !toastui.Editor) {
        console.error('Toast UI Editor 라이브러리가 로드되지 않았습니다.');
        alert('에디터 라이브러리를 불러오는 중 오류가 발생했습니다. 페이지를 새로고침해주세요.');
        return;
    }
    
    // 기존 에디터가 있으면 제거
    if (noticeEditor) {
        try {
            noticeEditor.destroy();
        } catch (e) {
            console.warn('기존 에디터 제거 중 오류:', e);
        }
        noticeEditor = null;
    }
    
    // Toast UI Editor 초기화
    const editorEl = document.getElementById('noticeEditor');
    if (!editorEl) {
        console.error('noticeEditor 요소를 찾을 수 없습니다.');
        return;
    }
    
    // 에디터가 이미 초기화되어 있는지 확인
    if (editorEl.querySelector('.toastui-editor')) {
        console.log('에디터가 이미 초기화되어 있습니다.');
        // 기존 에디터 제거 후 재초기화
        editorEl.innerHTML = '';
    }
    
    try {
        noticeEditor = new toastui.Editor({
            el: editorEl,
            height: '500px',
            initialEditType: 'wysiwyg',
            previewStyle: 'vertical',
            placeholder: '공지 내용을 입력하세요...',
            toolbarItems: [
                ['heading', 'bold', 'italic', 'strike'],
                ['hr', 'quote'],
                ['ul', 'ol', 'task', 'indent', 'outdent'],
                ['table', 'image', 'link'],
                ['code', 'codeblock'],
                ['scrollSync']
            ],
            hooks: {
                addImageBlobHook: async (blob, callback) => {
                    const formData = new FormData();
                    formData.append('image', blob);
                    try {
                        const res = await csrfFetch('/upload-image', {
                            method: 'POST',
                            body: formData
                        });
                        const result = await res.json();
                        callback(result.url, '업로드 이미지');
                    } catch (e) {
                        alert("이미지 업로드 실패");
                    }
                }
            }
        });
        
        // 폼 제출 처리 - 이벤트 위임 사용 (중복 방지)
        // 초기화 버튼 처리
        const resetBtn = document.getElementById('noticeResetBtn');
        if (resetBtn) {
            resetBtn.onclick = function() {
                const form = document.getElementById('noticeForm');
                if (form) form.reset();
                if (noticeEditor) {
                    noticeEditor.reset();
                }
            };
        }
        
        console.log('공지 작성 페이지가 초기화되었습니다.');
    } catch (error) {
        console.error('Toast Editor 초기화 실패:', error);
        alert('에디터 초기화에 실패했습니다. 페이지를 새로고침해주세요.');
    }
}

// 공지 폼 제출 처리 (이벤트 위임)
document.addEventListener('submit', function(e) {
    if (e.target && e.target.id === 'noticeForm') {
        e.preventDefault();
        handleNoticeSubmit(e.target);
    }
});

function handleNoticeSubmit(form) {
    const formData = new FormData(form);
    const title = formData.get('title')?.trim();
    
    // 유효성 검사
    if (!title) {
        showNoticeMessage('제목을 입력해주세요.', 'error');
        return;
    }
    
    // 에디터 내용 가져오기
    if (!noticeEditor) {
        showNoticeMessage('에디터를 초기화하는 중 오류가 발생했습니다.', 'error');
        return;
    }
    
    const content = noticeEditor.getMarkdown();
    if (!content || content.trim().length === 0) {
        showNoticeMessage('내용을 입력해주세요.', 'error');
        return;
    }
    
    formData.set('content', content);
    
    // API 호출
    csrfFetch('/admin/notice/save', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            return response.text();
        } else {
            return response.text().then(errorText => {
                throw new Error(errorText || '공지 저장에 실패했습니다.');
            });
        }
    })
    .then(message => {
        showNoticeMessage(message || '공지가 성공적으로 등록되었습니다.', 'success');
        // 폼 초기화
        document.getElementById('noticeForm').reset();
        noticeEditor.reset();
    })
    .catch(error => {
        console.error('공지 저장 실패:', error);
        showNoticeMessage(error.message || '공지 저장에 실패했습니다.', 'error');
    });
}

// 공지 메시지 표시 함수
function showNoticeMessage(message, type = 'info') {
    const messageDiv = document.getElementById('noticeAddMessage');
    if (!messageDiv) {
        console.warn('noticeAddMessage 요소를 찾을 수 없습니다.');
        return;
    }
    
    messageDiv.textContent = message;
    messageDiv.className = `message ${type}`;
    messageDiv.style.display = 'block';
    
    const hideTime = type === 'success' ? 5000 : 3000;
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, hideTime);
}


// 공지 리스트 표시 함수
function initNoticeList() {
    const noticeList = document.getElementById('noticeList');
    const noNoticeMessage = document.getElementById('noNoticeMessage');
    
    if (!noticeList) {
        console.error('noticeList 요소를 찾을 수 없습니다.');
        return;
    }
    
    // 로딩 상태 표시
    noticeList.innerHTML = '<div class="loading">공지 목록을 불러오는 중...</div>';
    if (noNoticeMessage) noNoticeMessage.style.display = 'none';
    
    // API 호출하여 공지 목록 가져오기
    csrfFetch('/admin/notice/list')
        .then(response => response.json())
        .then(noticePosts => {
            if (!noticePosts || noticePosts.length === 0) {
                noticeList.innerHTML = '';
                if (noNoticeMessage) noNoticeMessage.style.display = 'block';
            } else {
                renderNoticeList(noticePosts);
                if (noNoticeMessage) noNoticeMessage.style.display = 'none';
            }
        })
        .catch(error => {
            console.error('공지 리스트 로드 실패:', error);
            noticeList.innerHTML = '<div class="error">공지 목록을 불러오는데 실패했습니다.</div>';
        });
}

// 공지 목록 렌더링 함수
function renderNoticeList(noticePosts) {
    const noticeList = document.getElementById('noticeList');
    if (!noticeList) return;
    
    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${month}-${day}`;
    };
    
    // 게시판 목록과 동일한 구조로 렌더링
    noticeList.innerHTML = noticePosts.map(post => {
        const title = (post.title || '제목 없음').replace(/'/g, "&#39;").replace(/"/g, "&quot;");
        const nickname = (post.nickname || '작성자 없음').replace(/'/g, "&#39;").replace(/"/g, "&quot;");
        const categoryName = (post.categoryName || '공지').replace(/'/g, "&#39;").replace(/"/g, "&quot;");
        const commentCount = post.commentCount || 0;
        const viewCount = post.viewCount || 0;
        const likeCount = post.likeCount || 0;
        const createdAt = formatDate(post.createdAt);
        const postId = post.postId;
        
        return `
            <div class="post-row notice-row">
                <span class="category notice-category">${categoryName}</span>
                <span class="title with-author">
                    <a href="/board/list/detail/${postId}?boardCode=99" target="_blank" title="${title}">
                        ${title} [${commentCount}]
                    </a>
                    <div class="by-author">by ${nickname}</div>
                </span>
                <span class="date">${createdAt}</span>
                <span class="views">${viewCount}</span>
                <span class="likes">${likeCount}</span>
                <span class="admin-actions">
                    <button type="button" class="btn btn-sm btn-primary" onclick="editNotice(${postId})" style="margin-right: 5px;">수정</button>
                    <button type="button" class="btn btn-sm btn-danger" onclick="deleteNotice(${postId}, '${title.replace(/'/g, "\\'")}')">삭제</button>
                </span>
                
                <span class="title mobile-title mobile">
                    <a href="/board/list/detail/${postId}?boardCode=99" target="_blank" title="${title}">
                        ${title} [${commentCount}]
                    </a>
                </span>
                
                <div class="meta mobile">
                    <span class="author">${nickname}</span>
                    <span class="date">${createdAt}</span>
                    <span class="views">${viewCount}</span>
                    <span class="likes">${likeCount}</span>
                    <span class="admin-actions-mobile">
                        <button type="button" class="btn btn-sm btn-primary" onclick="editNotice(${postId})" style="margin-right: 5px;">수정</button>
                        <button type="button" class="btn btn-sm btn-danger" onclick="deleteNotice(${postId}, '${title.replace(/'/g, "\\'")}')">삭제</button>
                    </span>
                </div>
            </div>
        `;
    }).join('');
}

// 공지 수정 함수
function editNotice(postId) {
    // TODO: 공지 수정 기능 구현
    alert('공지 수정 기능은 아직 구현되지 않았습니다.');
}

// 공지 삭제 함수
function deleteNotice(postId, title) {
    if (!confirm(`"${title}" 공지를 정말 삭제하시겠습니까?\n\n삭제된 공지는 복구할 수 없습니다.`)) {
        return;
    }
    
    // TODO: 공지 삭제 API 호출 구현
    alert('공지 삭제 기능은 아직 구현되지 않았습니다.');
}