package com.geonpil.service;

import com.geonpil.domain.BoardDTO;
import com.geonpil.domain.Comment;
import com.geonpil.mapper.BoardMapper;
import com.geonpil.mapper.CommentMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    private final CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public void addComment(Comment comment) {
        commentMapper.insertComment(comment);
    }

    public List<Comment> getComments(Long postId) {
        return commentMapper.getCommentsByPostId(postId);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.findById(commentId);
        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
        commentMapper.softDeleteComment(commentId);
    }

    public void updateComment(Long id, String content, Long userId) {
        Comment comment = commentMapper.findById(id);
        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("권한 없음");
        }
        commentMapper.updateComment(id, content);
    }

}