package com.geonpil.mapper.board;

import com.geonpil.domain.board.BoardType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardTypeMapper {
    List<BoardType> getAllBoardTypes();
}


