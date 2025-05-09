package com.geonpil.controller;

import com.geonpil.domain.Comment;
import com.geonpil.security.CustomUserDetails;
import com.geonpil.service.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> addComment(@ModelAttribute Comment comment,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        Map<String, Object> result = new HashMap<>();

        if (userDetails == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }
        comment.setUserId(userDetails.getId());
        comment.setNickname(userDetails.getNickname());
        commentService.addComment(comment);

        result.put("success", true);
        result.put("message", "댓글 등록이 완료됐어요!");
        result.put("url", "댓글 등록이 완료됐어요!");
        return result;
    }

    // 2. 게시글 ID로 댓글 목록 조회
    @GetMapping("/{postId}")
    public List<Comment> getComments(@PathVariable Long postId) {
        return commentService.getComments(postId);
    }

    // 3. 댓글 삭제 (soft delete)
    @PostMapping("/delete/{commentId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public String deleteComment(@PathVariable Long commentId,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getId());
        return "success";
    }


    // 4. 댓글 수정
    @PostMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public String editComment(@PathVariable Long id,
                              @RequestParam String content,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.updateComment(id, content, userDetails.getUser().getId());
        return "success";
    }
}
