package com.geonpil.controller.admin;

import com.geonpil.domain.Category;
import com.geonpil.domain.admin.BookPick;
import com.geonpil.domain.board.BoardType;
import com.geonpil.dto.bookPick.BookPickWithBookInfo;
import com.geonpil.service.CategoryService;
import com.geonpil.service.admin.BookPickService;
import com.geonpil.service.board.BoardService;
import com.geonpil.service.board.BoardTypeService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookPickService bookPickService;
    private final CategoryService categoryService;
    private final BoardService boardService;
    private final BoardTypeService boardTypeService;
    // 관리자 메인 페이지
    @GetMapping("/main")    
    public String list(Model model) {
        return "admin/admin";
    }

    // 북픽 관련 fragment
    @GetMapping("/book-picks/fragment/add")
    public String getAddBookPickFragment(Model model) {
        return "admin/_book-pick-add-fragment :: bookPickAddFormFragment";
    }

    @GetMapping("/book-picks/fragment/delete")
    public String getDelBookPickFragment(Model model) {
        List<BookPickWithBookInfo> bookPicks = bookPickService.getAllBookPicks();
        model.addAttribute("bookPicks", bookPicks);
        return "admin/_book-pick-delete-fragment :: bookPickDeleteFormFragment";
    }

    // 카테고리 관련 fragment
    @GetMapping("/categories/fragment/add")
    public String getCategoryAddFragment(Model model) {

        List<BoardType> boardTypes = boardTypeService.getAllBoardTypes();
        model.addAttribute("boardTypes", boardTypes);
        return "admin/_category-add-fragment :: categoryAddFormFragment";
    }

    @GetMapping("/categories/fragment/delete")
    public String getCategoryDeleteFragment(Model model) {
        List<BoardType> boardTypes = boardTypeService.getAllBoardTypes();
        model.addAttribute("boardTypes", boardTypes);
        return "admin/_category-delete-fragment :: categoryDeleteFormFragment";
    }

    // 카테고리 관련 REST API
    @GetMapping("/categories")
    @ResponseBody
    public List<Category> getCategoriesByBoardCode(@RequestParam int boardCode) {
        return categoryService.getCategoriesByBoardCode(boardCode);
    }

    @PostMapping("/categories")
    @ResponseBody
    public ResponseEntity<String> addCategory(@RequestBody Map<String, Object> request) {
        try {
            int boardCode = (Integer) request.get("boardCode");
            String categoryName = ((String) request.get("categoryName")).trim();
            
            // 유효성 검사
            if (categoryName.isEmpty()) {
                return ResponseEntity.badRequest().body("카테고리 이름을 입력해주세요.");
            }
            
            if (categoryName.length() > 50) {
                return ResponseEntity.badRequest().body("카테고리 이름은 50자 이하로 입력해주세요.");
            }
            
            // 중복 검사
            if (categoryService.existsByNameAndBoardCode(boardCode, categoryName)) {
                return ResponseEntity.badRequest().body("이미 존재하는 카테고리입니다.");
            }
            
            // 카테고리 추가
            Category category = new Category();
            category.setBoardCode(boardCode);
            category.setCategoryName(categoryName);
            
            categoryService.addCategory(category);
            
            return ResponseEntity.ok("카테고리가 성공적으로 추가되었습니다.");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("카테고리 추가 중 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/categories/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            // 사용 중인 카테고리 확인
            int postCount = categoryService.countPostsUsingCategory(id);
            if (postCount > 0) {
                return ResponseEntity.badRequest().body("사용 중인 카테고리는 삭제할 수 없습니다. (연결된 게시글: " + postCount + "개)");
            }
            
            categoryService.deleteCategory(id);
            
            return ResponseEntity.ok("카테고리가 성공적으로 삭제되었습니다.");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("카테고리 삭제 중 오류가 발생했습니다.");
        }
    }

}