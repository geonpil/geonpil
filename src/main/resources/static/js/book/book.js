function fetchBookSearchResults(page, query) {
    const newUrl = `/api/search?query=${encodeURIComponent(query)}&page=${page}`;
    history.pushState({}, '', newUrl);

    fetch(`/api/search/fragment/result?query=${encodeURIComponent(query)}&page=${page}`)
        .then(res => res.text())
        .then(html => {
            document.querySelector("#book-list-area").innerHTML = html;
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });

    fetch(`/api/search/fragment/pagination?query=${encodeURIComponent(query)}&page=${page}`)
        .then(res => res.text())
        .then(html => {
            document.querySelector("#pagination-area").innerHTML = html;
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
}