package com.geonpil.service;

import com.geonpil.domain.Category;
import com.geonpil.mapper.board.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<Category> getCategoriesByBoardCode(int boardCode) {
        return categoryMapper.getCategoriesByBoardCode(boardCode);
    }

    /**
     * 카테고리 추가
     */
    public void addCategory(Category category) {
        categoryMapper.insertCategory(category);
    }

    /**
     * 카테고리 삭제
     */
    public void deleteCategory(Long categoryId) {
        categoryMapper.deleteCategoryById(categoryId);
    }

    /**
     * 게시판별 카테고리명 중복 검사
     */
    public boolean existsByNameAndBoardCode(int boardCode, String categoryName) {
        return categoryMapper.existsByNameAndBoardCode(boardCode, categoryName);
    }

    /**
     * 카테고리를 사용하는 게시글 수 조회
     */
    public int countPostsUsingCategory(Long categoryId) {
        return categoryMapper.countPostsUsingCategory(categoryId);
    }
}