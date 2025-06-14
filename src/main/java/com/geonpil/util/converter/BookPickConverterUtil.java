package com.geonpil.util.converter;

import com.geonpil.domain.Book;
import com.geonpil.domain.admin.BookPick;
import com.geonpil.domain.admin.BookPickEntity.BookPickEntity;
import com.geonpil.dto.bookDetail.BookDetailViewResponse;
import com.geonpil.dto.bookDetail.BookEntity;
import com.geonpil.util.IsbnUtil;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.geonpil.util.IsbnUtil.extractIsbn10;
import static com.geonpil.util.IsbnUtil.extractIsbn13;

public class BookPickConverterUtil {


    public static BookPickEntity BookPickToEntity(BookPick dto) {
        BookPickEntity entity = new BookPickEntity();
        entity.setBookId(dto.getBookId());
        entity.setBookPickId(dto.getBook_pick_id());
        entity.setReason(dto.getReason());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setIsVisible(dto.getIsVisible());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }


}
