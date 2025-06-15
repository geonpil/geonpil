package com.geonpil.util.converter;

import com.geonpil.domain.admin.BookPick;
import com.geonpil.domain.admin.bookPickEntity.BookPickEntity;

public class BookPickConverterUtil {


    public static BookPickEntity BookPickToEntity(BookPick dto) {
        BookPickEntity entity = new BookPickEntity();
        entity.setBookId(dto.getBookId());
        entity.setBookPickId(dto.getBook_pick_id());
        entity.setReason(dto.getReason());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setIsDeleted(dto.getIsDeleted());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }


}
