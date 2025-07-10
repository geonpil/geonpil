package com.geonpil.mapper.board;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.geonpil.domain.book.BoardAttachment;

import java.util.List;

@Mapper
public interface BoardAttachmentMapper {

    /**
     * 단일 첨부파일 저장
     */
    void insert(BoardAttachment attachment);

    /**
     * 특정 게시글의 첨부파일 목록 조회
     */
    List<BoardAttachment> findByPostId(@Param("postId") Long postId);

    /**
     * 첨부파일 소프트 삭제
     */
    void softDeleteById(@Param("attachmentId") Long attachmentId);

    /** 단일 첨부 조회 */
    BoardAttachment findById(@Param("attachmentId") Long attachmentId);
} 