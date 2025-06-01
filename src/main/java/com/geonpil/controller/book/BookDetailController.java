package com.geonpil.controller.book;

import com.geonpil.domain.Book;
import com.geonpil.domain.Review;
import com.geonpil.domain.ReviewComment;
import com.geonpil.domain.entity.BookEntity;
import com.geonpil.dto.review.ReviewCommentDto;
import com.geonpil.dto.review.ReviewResponseDto;
import com.geonpil.mapper.review.ReviewLikeMapper;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.UserService;
import com.geonpil.service.book.BookService;
import com.geonpil.service.review.ReviewCommentService;
import com.geonpil.service.review.ReviewService;
import com.geonpil.util.mapper.ReviewMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books") // RESTful URL 구조 개선
@RequiredArgsConstructor
public class BookDetailController {

    private final BookService bookService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final ReviewLikeMapper reviewLikeMapper;
    private final ReviewMapperUtil reviewMapperUtil;
    private final ReviewCommentService reviewCommentService;

    @GetMapping("/{bookId}")
    public String getBookDetail(@PathVariable Long bookId
                                ,@AuthenticationPrincipal AppUserInfo user
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
        for (ReviewResponseDto reviewDto : reviewDtos) {
            List<ReviewComment> comments = reviewCommentService.getCommentsByReviewId(reviewDto.getReviewId());
            List<ReviewCommentDto> commentDtos = comments.stream()
                    .map(comment -> ReviewCommentDto.builder()
                            .reviewCommentId(comment.getReviewCommentId())
                            .reviewId(comment.getReviewId())
                            .parentId(comment.getParentId())
                            .userId(comment.getUserId())
                            .username(userService.getUserNicknameByUserId(comment.getUserId()))
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());
            reviewDto.setReviewCommentDtos(commentDtos);
        }


        // 4. 평균 평점 계산 (없으면 0)
        double averageRating = reviewService.calculateAverageRating(reviews);

        // 5. 모델에 데이터 주입
        model.addAttribute("book", book);
        model.addAttribute("reviews", reviewDtos);
        model.addAttribute("avgRating", averageRating);
        if (user != null) {
            model.addAttribute("currentUserId", user.getId());
        }

        return "book/detail/detail"; // detail.html
    }


    @GetMapping("/isbn/{isbn}")
    public String fetchOrRedirect(@PathVariable String isbn) {
        Book book = bookService.getOrFetchBookByIsbn(isbn);
        return "redirect:/books/" + book.getBookId();
    }
}
