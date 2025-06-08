package com.geonpil.controller.review;

import com.geonpil.domain.Review;
import com.geonpil.dto.review.ReviewResponseDto;
import com.geonpil.mapper.review.ReviewLikeMapper;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.user.UserService;
import com.geonpil.service.review.ReviewCommentService;
import com.geonpil.service.review.ReviewLikeService;
import com.geonpil.service.review.ReviewService;
import com.geonpil.util.converter.ReviewConverterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewViewController {

    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;
    private final UserService userService;
    private final ReviewLikeMapper reviewLikeMapper;
    private final ReviewConverterUtil reviewMapperUtil;
    private final ReviewCommentService reviewCommentService;

    // 특정 책의 모든 리뷰 조회
    @GetMapping("/{bookId}")
    public String getReviews(@PathVariable Long bookId,
                             @RequestParam(defaultValue = "recent") String sort,
                             @AuthenticationPrincipal AppUserInfo user,
                           Model model
                            ) {
        List<Review> reviews = reviewService.getReviewsByBookId(bookId, sort);

        List<ReviewResponseDto> reviewDtos = reviewMapperUtil.convertToResponseDto(reviews, user);
        reviewMapperUtil.attachCommentsToReviews(reviewDtos);
        List<ReviewResponseDto> visibleReviews = reviewMapperUtil.filterVisibleReviewsWithComments(reviewDtos);


        model.addAttribute("reviews", visibleReviews);
        if (user != null) {
            model.addAttribute("currentUserId", user.getId());
        }

        return "book/detail/_review-list-fragment :: reviewListFragment";
    }
}
