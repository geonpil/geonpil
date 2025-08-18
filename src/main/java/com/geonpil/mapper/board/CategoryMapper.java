package com.geonpil.mapper.board;

import com.geonpil.domain.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> getCategoriesByBoardCode(int boardCode);
    
    void insertCategory(Category category);
    
    void deleteCategoryById(Long categoryId);
    
    boolean existsByNameAndBoardCode(@Param("boardCode") int boardCode, @Param("categoryName") String categoryName);
    
    int countPostsUsingCategory(Long categoryId);
}
