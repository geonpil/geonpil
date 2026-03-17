package com.geonpil.mapper.board;

import com.geonpil.domain.ContestPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContestMapper {
    void insertContestPost(ContestPost contest);


    ContestPost findContestById(Long postId);



    List<ContestPost> findContestsByPage(@Param("offset") int offset,
                                         @Param("size") int size,
                                         @Param("categoryIds") List<Long> categoryIds,
                                         @Param("sort") String sort,
                                        @Param("isClosedIncluded") boolean isClosedIncluded)
            ;

    void updateContestPost(ContestPost contestPost);

    void updatePosterUrl(@Param("postId") Long postId, @Param("posterUrl") String posterUrl);

    List<ContestPost> findLatestContest(int limit);


    void insertContestCategory(@Param("postId") long postId,
                               @Param("categoryId") long categoryId);


    int contestCountAll(@Param("boardCode") int boardCode
            , @Param("categoryIds") List<Long> categoryIds
            , @Param("isClosedIncluded") boolean isClosedIncluded);



    void deleteCategoriesByPostId(Long postId);


    List<ContestPost> findAllContestForIndexing();


    List<ContestPost> findContestsByPostIds(@Param("postIds") List<Long> postIds);

}





