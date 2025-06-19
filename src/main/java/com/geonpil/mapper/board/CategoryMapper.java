package com.geonpil.mapper.board;

import com.geonpil.domain.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> getCategoriesByBoardCode(int boardCode);
}
