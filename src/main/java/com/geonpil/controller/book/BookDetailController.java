package com.geonpil.controller.book;

import com.geonpil.domain.Book;
import com.geonpil.domain.Review;
import com.geonpil.dto.review.ReviewResponseDto;
import com.geonpil.mapper.review.ReviewLikeMapper;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.user.UserService;
import com.geonpil.service.book.BookService;
import com.geonpil.service.review.ReviewCommentService;
import com.geonpil.service.review.ReviewService;
import com.geonpil.util.converter.ReviewConverterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/books") // RESTful URL 구조 개선
@RequiredArgsConstructor
public class BookDetailController {

    private final BookService bookService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final ReviewLikeMapper reviewLikeMapper;
    private final ReviewConverterUtil reviewMapperUtil;
    private final ReviewCommentService reviewCommentService;

    @GetMapping("/{bookId}")
    public String getBookDetail(@PathVariable Long bookId
                                ,@AuthenticationPrincipal AppUserInfo user
                                , @RequestParam String query
                                , @RequestParam(defaultValue = "1") int page
                                ,Model model) {
        // 1. 책 상세 정보 조회
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            return "error/404"; // 책이 없는 경우 예외 처리
        }


        // 2. 리뷰 목록 조회

        List<Review> reviews = reviewService.getReviewsByBookId(bookId);
        List<ReviewResponseDto> reviewDtos = reviewMapperUtil.convertToResponseDto(reviews, user);


        // 3. 각 리뷰에 댓글 리스트 설정
        reviewMapperUtil.attachCommentsToReviews(reviewDtos);
        List<ReviewResponseDto> visibleReviews = reviewMapperUtil.filterVisibleReviewsWithComments(reviewDtos);


        // 4. 평균 평점 계산 (없으면 0)
        double averageRating = reviewService.getAverageRating(bookId);

        // 5. 모델에 데이터 주입
        model.addAttribute("book", book);
        model.addAttribute("query", query);
        model.addAttribute("page", page);
        model.addAttribute("reviews", visibleReviews);
        model.addAttribute("avgRating", averageRating);
        if (user != null) {
            model.addAttribute("currentUserId", user.getId());
        }

        return "book/detail/detail"; // detail.html
    }


    @GetMapping("/isbn/{isbn}")
    public String fetchOrRedirect(@PathVariable String isbn
                                 ,@RequestParam(required = false) String query
                                 , @RequestParam(required = false, defaultValue = "1") int page ) {
        Book book = bookService.getOrFetchBookByIsbn(isbn);
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);


        return "redirect:/books/" + book.getBookId() + "?query=" + encodedQuery + "&page=" + page;
    }
}
