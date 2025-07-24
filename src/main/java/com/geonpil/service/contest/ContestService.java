package com.geonpil.service.contest;

import com.geonpil.domain.*;
import com.geonpil.mapper.board.BoardMapper;
import com.geonpil.mapper.board.ContestMapper;
import com.geonpil.service.board.BoardService;
import com.geonpil.service.board.BoardSearchService;
import com.geonpil.util.DdayUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContestService {

    private final BoardMapper boardMapper;
    private final ContestMapper contestMapper;
    private final BoardService boardService;
    private final ContestSearchService contestSearchService;
    private final BoardSearchService boardSearchService;

    public ContestService(BoardMapper boardMapper, ContestMapper contestMapper, BoardService boardService, ContestSearchService contestSearchService, BoardSearchService boardSearchService) {
        this.boardMapper = boardMapper;
        this.contestMapper = contestMapper;
        this.boardService = boardService;
        this.contestSearchService = contestSearchService;
        this.boardSearchService = boardSearchService;
    }


    @Transactional
    public void saveContest(ContestPost contest, List<Long> categoryIds) {
        boardMapper.insertBoard(contest);           // BoardDTO 필드 처리
        contestMapper.insertContestPost(contest);   // contest_post 필드 처리
        contestSearchService.index(contest);
        long postId = contest.getPostId();

        for (Long categoryId : categoryIds) {
            contestMapper.insertContestCategory(postId, categoryId); // 카테고리 필드 처리
        }
    }


    public void updateContestPost(ContestPost contestPost,
                                  Long id) {
        Long postId = contestPost.getPostId();
        List<Long> categoryIds = contestPost.getCategoryIds();

        if (!contestPost.getUserId().equals(id)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }





        boardService.updatePost(postId, contestPost, id); // 게시글수정

        contestMapper.deleteCategoriesByPostId(postId);


        for(Long categoryId : categoryIds){
            contestMapper.insertContestCategory(postId, categoryId);
        }

        contestMapper.updateContestPost(contestPost);

        //ES 업데이트
        boardSearchService.updateIndex(contestPost);
        contestSearchService.updateIndex(contestPost);

    }

    public ContestPost findContestById(Long postId) {
        ContestPost contestPost = contestMapper.findContestById(postId);
        contestPost.setDDay(DdayUtils.calculateDDay(contestPost.getEndDate()));
        return contestPost;
    }

    public List<ContestPost> findContestsByPage(int page, int size, List<Long> categoryIds, String sort,boolean isClosedIncluded) {
        int offset = (page - 1) * size;
        List<ContestPost> contestPosts = contestMapper.findContestsByPage(offset, size, categoryIds, sort, isClosedIncluded);

        for (ContestPost post : contestPosts) {
            post.setDDay(DdayUtils.calculateDDay(post.getEndDate()));
        }

        return contestPosts;
    }

    public int getTotalPageCount(int pageSize, int boardCode, List<Long> categoryIds, boolean isClosedIncluded) {

        int totalCount = Math.max(1, contestMapper.contestCountAll(boardCode, categoryIds, isClosedIncluded));

        System.out.println("totalcount" + totalCount);
        return (int) Math.ceil((double) totalCount / pageSize);
    }


    public List<ContestPost> findLatestContest(int limit) {
        List<ContestPost> contestPosts = contestMapper.findLatestContest(limit);

        //dday 설정
        for (ContestPost post : contestPosts) {
            post.setDDay(DdayUtils.calculateDDay(post.getEndDate()));
        }

        return contestPosts;
    }
}