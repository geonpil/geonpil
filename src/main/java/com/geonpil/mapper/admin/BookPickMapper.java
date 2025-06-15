package com.geonpil.mapper.admin;


import com.geonpil.domain.admin.bookPickEntity.BookPickEntity;
import com.geonpil.dto.bookPick.BookPickWithBookInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookPickMapper {
    void insertBookPick(BookPickEntity bookPickEntity);

    void softDeleteBookPickById(String bookPickId);

    List<BookPickWithBookInfo> findAllBookPicks();

    BookPickEntity findBookPickByBookId(Long bookId);
}
