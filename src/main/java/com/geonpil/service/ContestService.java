package com.geonpil.service;

import com.geonpil.domain.*;
import com.geonpil.mapper.BoardMapper;
import com.geonpil.mapper.CategoryMapper;
import com.geonpil.mapper.ContestMapper;
import com.geonpil.mapper.PostLikeMapper;
import com.geonpil.security.CustomUserDetails;
import com.geonpil.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContestService {

    private final BoardMapper boardMapper;
    private final ContestMapper contestMapper;
    private final BoardService boardService;

    public ContestService(BoardMapper boardMapper, ContestMapper contestMapper, BoardService boardService) {
        this.boardMapper = boardMapper;
        this.contestMapper = contestMapper;
        this.boardService = boardService;
    }


    @Transactional
    public void saveContest(ContestPost contest) {
        boardMapper.insertBoard(contest);           // BoardDTO 필드 처리
        contestMapper.insertContestPost(contest);   // contest_post 필드 처리
    }


    public List<ContestPost> findContestByPage(){
        List<ContestPost> contestPosts = contestMapper.findAllContests();
            
        //dday 설정
        for(ContestPost post : contestPosts){
            post.setDDay(DateUtils.calculateDDay(post.getEndDate()));
        }

        return contestPosts;
    }

    public void updateContestPost(ContestPost contestPost,
                                  Long id){
        Long postId = contestPost.getPostId();

        if (!contestPost.getUserId().equals(id)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        boardService.updatePost(postId, contestPost, id);
        contestMapper.updateContestPost(contestPost);

    }

    public ContestPost findContestById(Long postId){
        ContestPost contestPost = contestMapper.findContestById(postId);
        contestPost.setDDay(DateUtils.calculateDDay(contestPost.getEndDate()));
        return contestPost;
    }

    public List<ContestPost> findContestsByPage(int page, int size) {
        int offset = (page - 1) * size;

        return contestMapper.findContestsByPage(offset, size);
    }

    public int getTotalPageCount(int pageSize, int boardCode) {
        int totalCount = boardMapper.countAll(boardCode);
        return (int) Math.ceil((double) totalCount / pageSize);
    }



}