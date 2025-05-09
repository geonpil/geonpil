package com.geonpil.mapper;

import com.geonpil.domain.Comment;
import com.geonpil.service.CommentService;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    void insertComment(Comment comment);

    List<Comment> getCommentsByPostId(Long postId);

    void softDeleteComment(Long commentId);

    Comment findById(Long commentId);

    public void updateComment(Long id, String content);

}
