
const selectedBook = document.querySelector("#selected-book > div");
const selectedIsbn = document.getElementById("isbn");

document.querySelector(".search-content").addEventListener("click", (e) => {
    const book = e.target.closest(".selectable-book");
    if(book){
        const selectedBookIsbn = book.dataset.bookIsbn;
        const selectedBookTitle = book.dataset.bookTitle;
        const selectedBookAuthors = book.dataset.bookAuthors;

        selectedIsbn.value =  selectedBookIsbn;

        selectedBook.innerHTML = `<div>${selectedBookIsbn}</div>
                                   <div>${selectedBookTitle}</div>
                                   <div>${selectedBookAuthors}</div>`;

    }
})