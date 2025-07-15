package com.geonpil.controller.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.domain.Category;
import com.geonpil.domain.Comment;
import com.geonpil.domain.PageResult;
import com.geonpil.dto.commons.PageInfo;
import com.geonpil.resolver.BoardNameResolver;
import com.geonpil.security.AppUserInfo;
import com.geonpil.service.board.BoardService;
import com.geonpil.service.CategoryService;
import com.geonpil.service.CommentService;
import com.geonpil.service.board.BoardSearchService;
import com.geonpil.service.board.BoardAttachmentService;
import com.geonpil.domain.book.BoardAttachment;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.geonpil.util.PaginationUtil.buildPageInfo;


@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final BoardSearchService boardSearchService;
    private final BoardAttachmentService boardAttachmentService;

    public BoardController(BoardService boardService, CategoryService categoryService, CommentService commentService, BoardSearchService boardSearchService, BoardAttachmentService boardAttachmentService) {
        this.boardService = boardService;
        this.categoryService = categoryService;
        this.commentService = commentService;
        this.boardSearchService = boardSearchService;
        this.boardAttachmentService = boardAttachmentService;
    }


    @GetMapping("/list")
    public String list(@RequestParam("boardCode") int boardCode,
                       @RequestParam(required = false) List<Long> categoryIds,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                                                        Model model) {


        PageResult<BoardDTO> pageResult= boardService.findByPage(boardCode,categoryIds, page, 10);


        int pageGroupSize = 5; // 한 번에 보여줄 페이지 수
        int currentPage = page; // 요청받은 페이지
        int totalPages = pageResult.getTotalPages();   // 전체 페이지 수

        PageInfo pageInfo = buildPageInfo(currentPage, totalPages, pageGroupSize,"");


        List<Category> categories = categoryService.getCategoriesByBoardCode(boardCode);


        model.addAttribute("categories", categories);
        model.addAttribute("posts", pageResult.getPosts());
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("page", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("boardName", BoardNameResolver.resolve(boardCode));

        //페이지네이션 관련
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("action", "board"); // 또는 "book"
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
                           @RequestParam(value = "files", required = false) List<MultipartFile> files,
                           @AuthenticationPrincipal AppUserInfo user,
                           RedirectAttributes redirectAttributes) throws java.io.IOException {

        boardDTO.setUserId(user.getId());
        boardService.save(boardDTO);
        // 첨부파일 저장
        boardAttachmentService.saveFiles(boardDTO.getPostId(), files);
        boardSearchService.index(boardDTO);
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
        List<BoardAttachment> attachments = boardAttachmentService.getFiles(postId);


        model.addAttribute("post", post);
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("comments", comments);
        model.addAttribute("attachments", attachments);
        return "board/detail"; // templates/board/detail.html
    }

    
    // 게시글 삭제
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id,
                             @AuthenticationPrincipal AppUserInfo user,
                             @RequestParam int boardCode,
                             RedirectAttributes redirectAttributes) {
        boardService.softDeleteById(id, user.getId()); // 본인 확인 포함
        redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");

        if(boardCode == 3) return "redirect:/contest/list?boardCode=3";
        return "redirect:/board/list?boardCode=" + boardCode;
    }

    //게시글 좋아요
    @PostMapping("/like/{postId}")
    @PreAuthorize("isAuthenticated()")
    public String likePost(@PathVariable Long postId,
                           @AuthenticationPrincipal AppUserInfo user,
                           @RequestParam int boardCode,
                           RedirectAttributes redirectAttributes) {
        try {
            boardService.likePost(postId, user.getId());
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
                           @AuthenticationPrincipal AppUserInfo user,
                           @RequestParam int boardCode,
                           Model model) {
        BoardDTO post = boardService.findById(id);
        List<Category> categories = categoryService.getCategoriesByBoardCode(boardCode);
        List<BoardAttachment> attachments = boardAttachmentService.getFiles(id);
        if (!post.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        model.addAttribute("post", post);
        model.addAttribute("categories", categories);
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("attachments", attachments);
        return "board/edit";
    }

    // 수정 처리
    @PostMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editPost(@PathVariable Long id,
                           @ModelAttribute BoardDTO updatedPost,
                           @RequestParam(value="files", required = false) List<MultipartFile> files,
                           @RequestParam(value="deleteAttachmentIds", required = false) List<Long> deleteAttachmentIds,
                           @AuthenticationPrincipal AppUserInfo user
                           ) throws java.io.IOException {

        boardService.updatePost(id, updatedPost, user.getId());
        // 삭제 요청 처리
        boardAttachmentService.deleteFiles(deleteAttachmentIds, user.getId());
        // 새 파일 저장
        boardAttachmentService.saveFiles(id, files);
        return "redirect:/board/list/detail/" + id + "?boardCode=" + updatedPost.getBoardCode();
    }


    // 게시글 fragment 반환
    @GetMapping("/list/fragment")
    public String listFragment(@RequestParam("boardCode") int boardCode,
                               @RequestParam(required = false) List<Long> categoryIds,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(required = false) String keyword,
                               Model model) {
        int size = 10;

        PageResult<BoardDTO> pageResult = boardService.findByPage(boardCode, categoryIds, page, size);

        int pageGroupSize = 5; // 한 번에 보여줄 페이지 수
        int totalPages = pageResult.getTotalPages();   // 전체 페이지 수

        PageInfo pageInfo = buildPageInfo(page, totalPages, pageGroupSize, keyword);


        model.addAttribute("posts", pageResult.getPosts());
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("page", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());

        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("action", "board"); // 또는 "book"


        return "board/_search-result-fragment::searchResultFragment";
    }


    // 게시글 페이지네이션 fragment 반환
    @GetMapping("/list/fragment/pagination")
    public String getPaginationFragment(@RequestParam("boardCode") int boardCode,
                               @RequestParam(required = false) List<Long> categoryIds,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               Model model) {
        int size = 10;

        PageResult<BoardDTO> pageResult = boardService.findByPage(boardCode, categoryIds, page, size);

        int pageGroupSize = 5; // 한 번에 보여줄 페이지 수
        int currentPage = page; // 요청받은 페이지
        int totalPages = pageResult.getTotalPages();   // 전체 페이지 수

        PageInfo pageInfo = buildPageInfo(currentPage, totalPages, pageGroupSize,"");


        model.addAttribute("posts", pageResult.getPosts());
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("page", pageResult.getPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());

        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("action", "board"); // 또는 "book"


        return "commons/_pagination-fragment :: paginationFragment";
    }


}