package com.geonpil.service.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.domain.PageResult;
import com.geonpil.domain.PostLike;
import com.geonpil.mapper.board.BoardMapper;
import com.geonpil.mapper.board.PostLikeMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BoardService {

    private final BoardMapper boardMapper;
    private final PostLikeMapper postLikeMapper;

    public BoardService(BoardMapper boardMapper, PostLikeMapper postLikeMapper) {
        this.boardMapper = boardMapper;
        this.postLikeMapper = postLikeMapper;
    }

    public void save(BoardDTO boardDTO) {
        boardMapper.insertBoard(boardDTO);
    }


    public List<BoardDTO> findAll(int boardCode) {
        return boardMapper.findAll(boardCode);
    }

    public BoardDTO findById(Long postId) {
        return boardMapper.findById(postId);
    }


    @Transactional
    public void increaseViewCount(Long postId) {
        boardMapper.incrementViewCount(postId);
    }



    public void softDeleteById(Long postId, Long currentUserId) {
        BoardDTO post = boardMapper.findById(postId);
        if (post != null && post.getUserId().equals(currentUserId)) {
            boardMapper.softDeleteById(postId);
        } else {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
    }

    @Transactional
    public void likePost(Long postId, Long userId) {
        if (postLikeMapper.countByPostIdAndUserId(postId, userId) == 0) {
            PostLike like = PostLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .build();


            postLikeMapper.insert(like);
            postLikeMapper.incrementLikeCount(postId);
        } else {
            throw new IllegalStateException("이미 추천한 게시글입니다.");
        }
    }


    @Transactional
    public void updatePost(Long id, BoardDTO updatedPost, Long userId) {
        BoardDTO existing = boardMapper.findById(id);
        if (!existing.getUserId().equals(userId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        existing.setTitle(updatedPost.getTitle());
        existing.setContent(updatedPost.getContent());

        if(updatedPost.getCategoryId() != null) existing.setCategoryId(updatedPost.getCategoryId());

        existing.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        boardMapper.updatePost(existing);
    }


    public List<BoardDTO> getLatestPosts(int boardCode, int limit) {
        return boardMapper.findLatestByBoardCode(boardCode, limit);
    }

    public PageResult<BoardDTO> findByPage(int boardCode,List<Long> categoryIds, int page, int size) {
        int offset = (page - 1) * size;

        List<BoardDTO> posts = boardMapper.findByPage(boardCode, categoryIds, offset, size);
        int totalCount = boardMapper.countAll(boardCode, categoryIds);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        return new PageResult<>(posts, page, totalPages);
    }


    public List<BoardDTO> findByUserId(Long userId){
        return boardMapper.findByUserId(userId);
    }

}