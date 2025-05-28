package com.geonpil.controller.book;

import com.geonpil.domain.Book;
import com.geonpil.domain.Review;
import com.geonpil.domain.entity.BookEntity;
import com.geonpil.service.book.BookService;
import com.geonpil.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books") // RESTful URL 구조 개선
@RequiredArgsConstructor
public class BookDetailController {

    private final BookService bookService;
    private final ReviewService reviewService;

    @GetMapping("/{bookId}")
    public String getBookDetail(@PathVariable Long bookId, Model model) {
        // 1. 책 상세 정보 조회
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            return "error/404"; // 책이 없는 경우 예외 처리
        }

        // 2. 리뷰 목록 조회
        List<Review> reviews = reviewService.getReviewsByBookId(bookId);

        // 3. 평균 평점 계산 (없으면 0)
        double averageRating = reviews.isEmpty()
                ? 0.0
                : reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        // 4. 모델에 데이터 주입
        model.addAttribute("book", book);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", averageRating);

        return "book/detail/detail"; // detail.html
    }


    @GetMapping("/isbn/{isbn}")
    public String fetchOrRedirect(@PathVariable String isbn) {
        Book book = bookService.getOrFetchBookByIsbn(isbn);
        return "redirect:/books/" + book.getBookId();
    }
}
