package com.geonpil.mapper.review;

import com.geonpil.domain.Review;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewMapper {
    void insert(Review review);

    List<Review> findByBookId(Long bookId);

    Review findById(Long reviewId);

    void update(Review review);

    void softDelete(Long reviewId);
}
