document.querySelectorAll('.admin-menu').forEach(menu => {
    menu.addEventListener('click', (e) => {
        e.preventDefault();
        const action = menu.dataset.action;

        fetch(`/admin/book-picks/fragment/${action}`)
            .then(res => res.text())
            .then(html => {
                document.getElementById("admin-content").innerHTML = html;
                window.scrollTo({ top: 0, behavior: 'smooth' });

                if(action === 'add'){
                    initSearchBar();
                    initSearchableBookClickHandler();
                    initSubmitBookPick();
                }else if(action === 'delete'){
                    initDeleteBookPick();
                }

            })
            .catch(err => {
                alert("페이지를 불러오는 데 실패했습니다.");
                console.error(err);
            });
    });
});