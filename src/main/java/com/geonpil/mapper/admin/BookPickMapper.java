package com.geonpil.mapper.admin;

import com.geonpil.domain.Category;
import com.geonpil.domain.admin.BookPickEntity.BookPickEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookPickMapper {
    void insertBookPick(BookPickEntity bookPickEntity);
}
