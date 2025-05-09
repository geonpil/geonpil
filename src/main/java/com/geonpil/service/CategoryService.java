package com.geonpil.service;

import com.geonpil.domain.BoardDTO;
import com.geonpil.domain.Category;
import com.geonpil.mapper.BoardMapper;
import com.geonpil.mapper.CategoryMapper;
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
}