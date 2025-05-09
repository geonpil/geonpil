package com.geonpil.mapper;

import com.geonpil.domain.BoardDTO;
import com.geonpil.domain.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> getCategoriesByBoardCode(int boardCode);
}
