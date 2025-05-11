package com.geonpil.mapper;

import com.geonpil.domain.BoardDTO;
import com.geonpil.domain.Category;
import com.geonpil.domain.ContestPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContestMapper {
    void insertContestPost(ContestPost contest);

    List<ContestPost> findAllContests();

    ContestPost findContestById(Long postId);

    List<ContestPost> findContestsByPage(@Param("offset")int offset, @Param("size") int size);

    void updateContestPost(ContestPost contestPost);
    }
