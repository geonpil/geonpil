let selectedBookIsbn;


function initSearchableBookClickHandler() {
    const searchContent = document.querySelector(".search-content");
    if (!searchContent) {
        return;
    }

    searchContent.addEventListener("click", (e) => {
        const book = e.target.closest(".selectable-book");
        if (book) {
            selectedBookIsbn = book.dataset.bookIsbn;
            const selectedBookTitle = book.dataset.bookTitle;
            const selectedBookAuthors = book.dataset.bookAuthors;

            document.querySelector("#selected-book").children[1].innerHTML = `<div>${selectedBookIsbn}</div>
                                                                           <div>${selectedBookTitle}</div>
                                                                           <div>${selectedBookAuthors}</div>`;

            document.getElementById("reason").scrollIntoView({behavior: "smooth"});
        }


    });
}

function initSubmitBookPick() {
    const submit = document.getElementById("pick-submit");

    submit.addEventListener("click", () => {
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

        csrfFetch("/api/admin/book-picks", {
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
                document.querySelector("#selected-book > div").innerHTML =""
                document.querySelector("#book-list-area").innerHTML =""
            })
            .catch(err => {
                alert("등록 중 오류가 발생했습니다: " + err.message);
            });
    });

}



function initDeleteBookPick() {
    const delBtn = document.querySelectorAll(".pick-delete");

    delBtn.forEach( del => {
        del.addEventListener("click", (e) => {

            const id = del.dataset.id;

            csrfFetch(`/api/admin/book-picks/${id}`,{
                method : "PUT",
                headers: {
                    "Content-Type": "application/json"
                }
            })
                .then(res => {
                    if (!res.ok) throw new Error("삭제 실패");
                    return res.text();
                })
                .then(() => {
                    alert("삭제가 완료됐어요.")
                    document.getElementById(`book-pick-${id}`)?.remove();
                })
        })
    })

}