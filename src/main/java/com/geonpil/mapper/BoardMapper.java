package com.geonpil.mapper;

import com.geonpil.domain.BoardDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {
    void insertBoard(BoardDTO boardDTO);

    List<BoardDTO> findAll(int boardCode);

    BoardDTO findById(Long postNo);

    void softDeleteById(Long postId);

    void incrementViewCount(Long postId);

    void updatePost(BoardDTO boardDTO);

    List<BoardDTO> findLatestByBoardCode(@Param("boardCode") int boardCode, @Param("limit") int limit);

    List<BoardDTO> findByPage(@Param("boardCode") int boardCode,
                              @Param("categoryIds") List<Long> categoryIds,
                              @Param("offset") int offset,
                              @Param("size") int size);

    int countAll(@Param("boardCode") int boardCode,
                 @Param("categoryIds") List<Long> categoryIds);


    List<BoardDTO> findAllForIndexing();


    List<BoardDTO> findBoardsByPostIds(@Param("postIds") List<Long> postIds, @Param("boardCode") Integer boardCode);


}
