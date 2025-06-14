let selectedBookIsbn;


//선택시 화면에 제목/작가/isbn 표시
document.querySelector(".search-content").addEventListener("click", (e) => {
    const book = e.target.closest(".selectable-book");
    if (book) {
        selectedBookIsbn = book.dataset.bookIsbn;
        const selectedBookTitle = book.dataset.bookTitle;
        const selectedBookAuthors = book.dataset.bookAuthors;

        document.querySelector("#selected-book > div").innerHTML = `<div>${selectedBookIsbn}</div>
                                                                           <div>${selectedBookTitle}</div>
                                                                           <div>${selectedBookAuthors}</div>`;

    }

})

document.getElementById("pick-submit").addEventListener("click", () => {
    if (!selectedBookIsbn) {
        alert("책을 먼저 선택해주세요!");
        return;
    }
    alert(selectedBookIsbn)

    const reason = document.getElementById("reason").value;
    const displayOrder = document.getElementById("displayOrder").value;

    const data = {
        isbn: selectedBookIsbn,
        reason: reason,
        displayOrder: parseInt(displayOrder),
        isVisible: true
    };

    fetch("/admin/book-picks/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
        .then(res => {
            if (!res.ok) throw new Error("요청 실패");
            return res.text(); // 또는 res.json()
        })
        .then(() => {
            alert("등록이 완료됐습니다");
            // 필요시 입력값 초기화
            document.getElementById("reason").value = "";
            document.getElementById("displayOrder").value = 0;
        })
        .catch(err => {
            alert("등록 중 오류가 발생했습니다: " + err.message);
        });
});