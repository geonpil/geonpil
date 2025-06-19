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

    // 세팅한파라미터 push state(뒤로가기용)
   // const newUrl = `/board/list?` + params.toString();
    let url ="";
    let urlForPage =""


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



/*    fetch(url)
        .then(res => res.text())
        .then(html => {
            document.getElementById("post-list").innerHTML = html;
        });

    fetch(urlForPage)
        .then(res => res.text())
        .then(html => {
            document.getElementById("pagination-area").innerHTML = html;
        });*/
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



/*window.addEventListener("DOMContentLoaded", () => {

    console.log("카테고리 세팅");

    const urlParams = new URLSearchParams(window.location.search);
    const categoryParam = urlParams.get("categoryIds");
    const categoryIds = categoryParam ? categoryParam.split(",") : [];

    if(!categoryParam){
        initSelectAllCategory();
    }

    selectedCategories = new Set(categoryIds);
    const page = parseInt(urlParams.get("page")) || 1;

    // ✅ 선택된 카테고리 복원 (선택사항)
    highlightSelectedButtons(); // UI 반영 함수

    fetchBoardPosts(page, selectedCategories);
});*/


function restoreBoardUI(categoryIds = [], page = 1) {

    console.log("카테고리 세팅");

    if(!categoryIds){
        initSelectAllCategory();
    }
   // selectedCategories = new Set(categoryIds);

   // fetchBoardPosts(page, "",selectedCategories);

}



function toggleCategory(btn) {
    const id = btn.dataset.id;

    document.querySelector('.category-btn[data-id=""]').classList.remove("active");

        if (selectedCategories.has(id)) {
            selectedCategories.delete(id);
        } else {
            selectedCategories.add(id);
        }
    highlightSelectedButtons();



    console.log("토글 정상 작동")
    fetchBoardPosts(1,"", selectedCategories);
}


function selectAllCategories(btn) {
    // 모든 카테고리 버튼 비활성화
    document.querySelectorAll(".category-btn").forEach(b => b.classList.remove("active"));

    // 전체 버튼만 활성화
    btn.classList.add("active");

    // 선택된 카테고리 Set 비우기
    selectedCategories.clear();

    // 전체 게시글 요청
    fetchBoardPosts(page,null, selectedCategories);
}



function initSelectAllCategory(){
    // ✅ '전체' 버튼에 .active 클래스 부여
    const allBtn = document.querySelector('.category-btn[data-id=""]');
    if (allBtn) allBtn.classList.add("active");
};


