package com.geonpil.controller;

import com.geonpil.domain.BoardDTO;
import com.geonpil.domain.Category;
import com.geonpil.domain.Comment;
import com.geonpil.domain.PageResult;
import com.geonpil.service.BoardService;
import com.geonpil.service.CategoryService;
import com.geonpil.service.CommentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.geonpil.security.CustomUserDetails;

import java.util.List;


@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    public BoardController(BoardService boardService, CategoryService categoryService, CommentService commentService) {
        this.boardService = boardService;
        this.categoryService = categoryService;
        this.commentService = commentService;
    }


    @GetMapping("/list")
    public String list(@RequestParam("boardCode") int boardCode,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                                                        Model model) {

        int size = 10;
        PageResult<BoardDTO> pageResult= boardService.findByPage(boardCode,page,size);

        model.addAttribute("posts", pageResult.getPosts());
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("page", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        return "board/list"; // templates/board/list.html
    }

    //글쓰기 페이지로 이동
    @GetMapping("/write")
    public String write(@RequestParam("boardCode") int boardCode, Model model) {
        List<Category> categories = categoryService.getCategoriesByBoardCode(boardCode);

        model.addAttribute("categories", categories);
        model.addAttribute("boardCode", boardCode);

        return "board/write"; 
    }

    //글쓰기 저장
    @PostMapping("/write/save")
    public String savePost(@ModelAttribute BoardDTO boardDTO,
                           @AuthenticationPrincipal CustomUserDetails userDetail,
                           RedirectAttributes redirectAttributes) {
        boardDTO.setUserId(userDetail.getId());
        boardService.save(boardDTO);
        redirectAttributes.addFlashAttribute("message", "글이 성공적으로 등록되었습니다.");
        return "redirect:/board/list?boardCode=" + boardDTO.getBoardCode();
    }

    //글 상세보기
    @GetMapping("list/detail/{postId}")
    public String detail(@PathVariable Long postId,
                         @RequestParam String boardCode,
                                        Model model) {
        boardService.increaseViewCount(postId); // ⭐ 조회수 증가

        BoardDTO post = boardService.findById(postId);
        List<Comment> comments = commentService.getComments(postId);


        model.addAttribute("post", post);
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("comments", comments);
        return "board/detail"; // templates/board/detail.html
    }

    
    // 게시글 삭제
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        boardService.softDeleteById(id, userDetails.getId()); // 본인 확인 포함
        redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");
        return "redirect:/board/list?boardCode=1"; // 필요 시 boardCode 동적으로 변경
    }

    //게시글 좋아요
    @PostMapping("/like/{postId}")
    @PreAuthorize("isAuthenticated()")
    public String likePost(@PathVariable Long postId,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestParam int boardCode,
                           RedirectAttributes redirectAttributes) {
        try {
            boardService.likePost(postId, userDetails.getId());
            redirectAttributes.addFlashAttribute("message", "추천 완료!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/board/list/detail/" + postId +"?boardCode=" + boardCode;
    }

    // 게시글 수정
    @GetMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestParam int boardCode,
                           Model model) {
        BoardDTO post = boardService.findById(id);
        List<Category> categories = categoryService.getCategoriesByBoardCode(boardCode);
        if (!post.getUserId().equals(userDetails.getId())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        model.addAttribute("post", post);
        model.addAttribute("categories", categories);
        model.addAttribute("boardCode", boardCode);
        return "board/edit";
    }

    // 수정 처리
    @PostMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editPost(@PathVariable Long id,
                           @ModelAttribute BoardDTO updatedPost,
                           @AuthenticationPrincipal CustomUserDetails userDetails
                           ) {

        boardService.updatePost(id, updatedPost, userDetails.getId());
        return "redirect:/board/list/detail/" + id + "?boardCode=" + updatedPost.getBoardCode();
    }

}