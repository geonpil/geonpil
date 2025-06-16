package com.geonpil.service.board;

import com.geonpil.domain.BoardDTO;
import com.geonpil.elasticsearch.BoardDocument;
import com.geonpil.repository.search.BoardSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardSearchService {

    private final BoardSearchRepository boardSearchRepository;

    public void index(BoardDTO dto) {
        BoardDocument doc = new BoardDocument();
        doc.setPostId(dto.getPostId());
        doc.setUserId(dto.getUserId());
        doc.setBoardCode(dto.getBoardCode());
        doc.setCategoryId(dto.getCategoryId());
        doc.setTitle(dto.getTitle());
        doc.setContent(dto.getContent());
        doc.setViewCount(dto.getViewCount());
        doc.setLikeCount(dto.getLikeCount());
        doc.setCreatedAt(dto.getCreatedAt());

        boardSearchRepository.save(doc);
    }
}