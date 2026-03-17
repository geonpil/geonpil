package com.geonpil.controller.contest;

import com.geonpil.domain.*;
import com.geonpil.dto.commons.PageInfo;
import com.geonpil.service.board.BoardService;
import com.geonpil.service.CategoryService;
import com.geonpil.service.contest.ContestService;
import com.geonpil.service.board.BoardAttachmentService;
import com.geonpil.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.geonpil.util.PaginationUtil.buildPageInfo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.geonpil.security.AppUserInfo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.geonpil.domain.book.BoardAttachment;


@Controller
@RequestMapping("/contest")
public class ContestController {
    private final ContestService contestService;
    private final CategoryService categoryService;
    private final BoardService boardService;
    private final BoardAttachmentService boardAttachmentService;
    private final FileStorageService fileStorageService;


    public ContestController(ContestService contestService,
                             CategoryService categoryService,
                             BoardService boardService,
                             BoardAttachmentService boardAttachmentService,
                             FileStorageService fileStorageService) {
        this.contestService = contestService;
        this.categoryService = categoryService;
        this.boardService = boardService;
        this.boardAttachmentService = boardAttachmentService;
        this.fileStorageService = fileStorageService;
    }

    // 공모전 게시판 리스트
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam("boardCode") int boardCode) {
        int pageSize = 8;
        List<Long> categoryIds = new ArrayList<>();
        String sort = "recent";
        boolean isClosedIncluded = false;


        List<ContestPost> contests = contestService.findContestsByPage(page, pageSize, categoryIds, sort, isClosedIncluded);
        
        //카테고리목록 가져오기
        List<Category> categories = categoryService.getCategoriesByBoardCode(boardCode);

        // 전체페이지 가지고 오기
        int totalPages = contestService.getTotalPageCount(pageSize, boardCode, categoryIds, isClosedIncluded);

        PageInfo pageInfo = buildPageInfo(page, totalPages, 5,"");

        System.out.println("디버그 :" + pageInfo.getTotalPages());

        model.addAttribute("categories", categories);
        model.addAttribute("contests", contests);
        model.addAttribute("currentPage", page);
        model.addAttribute("boardCode",boardCode);

        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("action", "contest");

        return "contest/list";
    }

    // 글쓰기 페이지
    @GetMapping("/write")
    public String writeForm(Model model, @RequestParam("boardCode") int boardCode) {
        List<Category> categories = categoryService.getCategoriesByBoardCode(boardCode);

        model.addAttribute("boardCode", boardCode);
        model.addAttribute("categories", categories);

        return "contest/write";
    }

    // 글쓰기 상세
    @GetMapping("/detail/{id}")
    public String contestDetail(@PathVariable Long id,
                                @RequestParam(value = "boardCode", required = false) Integer boardCode,
                                Model model) {
        ContestPost post = contestService.findContestById(id);
        boardService.increaseViewCount(id);

        // 첨부파일 조회
        List<BoardAttachment> attachments = boardAttachmentService.getFiles(id);

        model.addAttribute("boardCode", boardCode);
        model.addAttribute("post", post);
        model.addAttribute("attachments", attachments);
        return "contest/detail";
    }

    // 글쓰기 수정
    @GetMapping("/edit/{id}")
    public String contestEdit(@PathVariable Long id,
                                @RequestParam(value = "boardCode", required = false) Integer boardCode,
                                Model model) {
        ContestPost post = contestService.findContestById(id);
        List<Category> categories = categoryService.getCategoriesByBoardCode(boardCode);
        List<BoardAttachment> attachments = boardAttachmentService.getFiles(id);
        model.addAttribute("categories", categories);
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("post", post);
        model.addAttribute("attachments", attachments);
        return "contest/edit";
    }

    // 수정 처리 (첨부파일 삭제/추가 포함)
    @PostMapping("/edit/{id}")
    public String updateContest(@PathVariable Long id,
                                @ModelAttribute ContestPost contestPost,
                                @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                                @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
                                @RequestParam(value = "existingPosterUrl", required = false) String existingPosterUrl,
                                @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                @RequestParam(value = "deleteAttachmentIds", required = false) List<Long> deleteAttachmentIds,
                                @AuthenticationPrincipal AppUserInfo user,
                                RedirectAttributes redirectAttributes) throws java.io.IOException {

        contestPost.setPostId(id);
        contestPost.setUserId(user.getId());
        if (categoryIds != null) {
            contestPost.setCategoryIds(categoryIds);
        }

        // 포스터 처리 (upload/contest/{id}/poster)
        if (posterFile != null && !posterFile.isEmpty()) {
            String subDir = "contest/" + id + "/poster";
            String storedName = fileStorageService.store(posterFile, subDir);
            String url = fileStorageService.buildFileUrl(subDir, storedName);
            contestPost.setPosterUrl(url);
        } else {
            contestPost.setPosterUrl(existingPosterUrl);
        }

        // 업데이트 (board + contest)
        contestService.updateContestPost(contestPost, user.getId());

        // 첨부파일 삭제 & 추가 (upload/contest/{id}/attachment)
        boardAttachmentService.deleteFiles(deleteAttachmentIds, user.getId());
        boardAttachmentService.saveFiles(id, files, "contest/" + id + "/attachment");

        redirectAttributes.addFlashAttribute("message", "수정이 완료되었습니다.");
        return "redirect:/contest/detail/" + id + "?boardCode=" + contestPost.getBoardCode();
    }

    // 글쓰기 저장 (첨부파일 포함)
    @PostMapping("/write/save")
    public String saveContest(@ModelAttribute ContestPost contestPost,
                              @RequestParam(value = "categoryIds", required = false) List<Long> categoryIds,
                              @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
                              @RequestParam(value = "files", required = false) List<MultipartFile> files,
                              @AuthenticationPrincipal AppUserInfo user,
                              RedirectAttributes redirectAttributes) throws java.io.IOException {

        // 작성자 세팅
        contestPost.setUserId(user.getId());

        // 카테고리 파라미터 보정
        if (categoryIds == null) {
            categoryIds = List.of();
        }

        // 게시글 + 공모전 정보 저장 (postId 생성됨)
        contestService.saveContest(contestPost, categoryIds);

        // 포스터 이미지 저장 (upload/contest/{postId}/poster)
        if (posterFile != null && !posterFile.isEmpty()) {
            String subDir = "contest/" + contestPost.getPostId() + "/poster";
            String storedName = fileStorageService.store(posterFile, subDir);
            String posterUrl = fileStorageService.buildFileUrl(subDir, storedName);
            contestService.updatePosterUrl(contestPost.getPostId(), posterUrl);
        }

        // 첨부파일 저장 (upload/contest/{postId}/attachment)
        if (files != null && !files.isEmpty()) {
            boardAttachmentService.saveFiles(contestPost.getPostId(), files, "contest/" + contestPost.getPostId() + "/attachment");
        }

        redirectAttributes.addFlashAttribute("message", "글이 성공적으로 등록되었습니다.");
        return "redirect:/contest/list?boardCode=" + contestPost.getBoardCode();
    }


    @GetMapping("/list/fragment")
    public String listFragment(@RequestParam("boardCode") int boardCode,
                               @RequestParam(required = false) List<Long> categoryIds,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam("sort") String sort,
                               @RequestParam("isClosedIncluded") boolean isClosedIncluded,
                               Model model) {
        int pageSize = 8;

        List<ContestPost> contests = contestService.findContestsByPage(page, pageSize, categoryIds, sort, isClosedIncluded);


        int totalPages = contestService.getTotalPageCount(pageSize, boardCode, categoryIds, isClosedIncluded);
        System.out.println("프래그먼트:" + totalPages);

        model.addAttribute("contests", contests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("boardCode",boardCode);


        return "contest/_contest-list-fragment :: contestListFragment";
    }

    @GetMapping("/list/fragment/pagination")
    public String getPaginationFragment(@RequestParam("boardCode") int boardCode,
                                       @RequestParam(required = false) List<Long> categoryIds,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam("sort") String sort,
                                       @RequestParam("isClosedIncluded") boolean isClosedIncluded,
                                       Model model) {
        int pageSize = 8;

        int totalPages = contestService.getTotalPageCount(pageSize, boardCode, categoryIds, isClosedIncluded);

        PageInfo pageInfo = buildPageInfo(page, totalPages, 5,"");
        System.out.println("디버그 :" + pageInfo.getTotalPages());

        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("action", "contest");

        return "commons/_pagination-fragment :: paginationFragment";
    }

    /**
     * 공모전 목록과 페이지네이션을 한번에 반환하는 통합 메서드
     */
    @GetMapping("/list/combined-fragment")
    public String getCombinedFragment(@RequestParam("boardCode") int boardCode,
                                     @RequestParam(required = false) List<Long> categoryIds,
                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam("sort") String sort,
                                     @RequestParam("isClosedIncluded") boolean isClosedIncluded,
                                     Model model) {
        int pageSize = 8;

        // 공모전 목록 조회
        List<ContestPost> contests = contestService.findContestsByPage(page, pageSize, categoryIds, sort, isClosedIncluded);

        // 총 페이지 수 계산
        int totalPages = contestService.getTotalPageCount(pageSize, boardCode, categoryIds, isClosedIncluded);

        // 페이지 정보 생성
        PageInfo pageInfo = buildPageInfo(page, totalPages, 5,"");

        // 모델에 데이터 추가
        model.addAttribute("contests", contests);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("action", "contest");

        // 검색 시 사용할 파라미터들도 추가
        model.addAttribute("sort", sort);
        model.addAttribute("isClosedIncluded", isClosedIncluded);
        model.addAttribute("categoryIds", categoryIds);

        return "contest/_contest-combined-fragment :: combinedContestFragment";
    }
}