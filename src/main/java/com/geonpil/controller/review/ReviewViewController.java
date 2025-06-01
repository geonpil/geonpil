package com.geonpil.controller.review;

import com.geonpil.domain.Review;
import com.geonpil.dto.review.ReviewRequestDto;
import com.geonpil.dto.review.ReviewResponseDto;
import com.geonpil.mapper.review.ReviewLikeMapper;
import com.geonpil.mapper.review.ReviewMapper;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.UserService;
import com.geonpil.service.review.ReviewLikeService;
import com.geonpil.service.review.ReviewService;
import com.geonpil.util.mapper.ReviewMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewViewController {

    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;
    private final UserService userService;
    private final ReviewLikeMapper reviewLikeMapper;
    private final ReviewMapperUtil reviewMapperUtil;

    // 특정 책의 모든 리뷰 조회 (선택적)
    @GetMapping("/{bookId}")
    public String getReviews(@PathVariable Long bookId,
                             @RequestParam(defaultValue = "recent") String sort,
                             @AuthenticationPrincipal AppUserInfo user,
                           Model model
                            ) {
        List<Review> reviews = reviewService.getReviewsByBookId(bookId, sort);

        List<ReviewResponseDto> reviewDtos = reviewMapperUtil.convertToResponseDto(reviews, user);

        model.addAttribute("reviews", reviewDtos);
        if (user != null) {
            model.addAttribute("currentUserId", user.getId());
        }

        return "book/detail/_review-list-fragment :: reviewListFragment";
    }

}
