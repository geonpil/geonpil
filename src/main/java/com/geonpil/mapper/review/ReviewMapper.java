package com.geonpil.mapper.review;

import com.geonpil.domain.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewMapper {
    List<Review> findByBookId(Long bookId);
    void insertReview(Review review);
    void softDeleteById(Long id);



}