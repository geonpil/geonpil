package com.geonpil.service.admin;

import com.geonpil.domain.Book;
import com.geonpil.domain.admin.BookPick;
import com.geonpil.domain.admin.BookPickEntity.BookPickEntity;
import com.geonpil.dto.bookDetail.BookEntity;
import com.geonpil.external.ExternalBookApiClient;
import com.geonpil.mapper.admin.BookPickMapper;
import com.geonpil.mapper.book.BookMapper;
import com.geonpil.util.converter.BookPickConverterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.geonpil.util.converter.BookConverterUtil.toEntity;

@Service
@RequiredArgsConstructor
public class BookPickService {

    private final BookMapper bookMapper;
    private final BookPickMapper bookPickMapper;
    private final ExternalBookApiClient externalBookApiClient;


    @Transactional
    public void saveBookPick(BookPick bookPick) {
        BookEntity bookEntity;

       String isbn = bookPick.getIsbn();

        if (isbn.length() == 13) { //13으로 검색
            bookEntity = bookMapper.findByIsbn13(isbn);
         } else {  bookEntity = bookMapper.findByIsbn10(isbn);   //10으로검색

        }

        BookPickEntity bookPickEntity =  BookPickConverterUtil.BookPickToEntity(bookPick);

        //이미 등록된 책이 있으면?
        if (bookEntity != null) {
            // -> 바로 bookpick insert
            bookPickEntity.setBookId(bookEntity.getBookId());
            bookPickMapper.insertBookPick(bookPickEntity);
        } else {
            //없으면 ? -> 책정보 가져와서 book + bookpick insert
            Book book = externalBookApiClient.fetchBookByIsbn(isbn);
            BookEntity fetchedBookEntity = toEntity(book);
            bookMapper.insertBook(fetchedBookEntity);
            //useGeneratedKeys 로 객체에 book_id가 자동주입됨
            bookPickEntity.setBookId(fetchedBookEntity.getBookId());
            
            bookPickMapper.insertBookPick(bookPickEntity);

        }


    }

}