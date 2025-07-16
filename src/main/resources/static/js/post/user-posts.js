document.addEventListener('DOMContentLoaded', function() {
    // 필요한 경우 페이지네이션이나 필터링 기능을 여기에 추가
}); 



function fetchUserPosts(page, query) {
    const apiUrl = `/my-posts?page=${page}`;

    csrfFetch(apiUrl, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then(response => {
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.text();
    })
}
