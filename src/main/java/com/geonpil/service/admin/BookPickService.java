package com.geonpil.service.admin;

import com.geonpil.domain.Book;
import com.geonpil.domain.admin.BookPick;
import com.geonpil.domain.admin.bookPickEntity.BookPickEntity;
import com.geonpil.dto.bookDetail.BookEntity;
import com.geonpil.dto.bookPick.BookPickWithBookInfo;
import com.geonpil.external.ExternalBookApiClient;
import com.geonpil.mapper.admin.BookPickMapper;
import com.geonpil.mapper.book.BookMapper;
import com.geonpil.util.converter.BookPickConverterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<BookPickWithBookInfo> getAllBookPicks() {

        return bookPickMapper.findAllBookPicks();

    }

    public BookPick getBookPickByBookId(Long bookId) {
        BookPick bookPick = new BookPick();
        BookPickEntity bookPickEntity = bookPickMapper.findBookPickByBookId(bookId);


        if (bookPickEntity == null) {
            return null; // 🔥 null로 반환
        }

        bookPick.setReason(bookPickEntity.getReason());


        return bookPick;
    }


    public void deleteBookPick(String BookPickId) {
        //삭제하는 mapper
        bookPickMapper.softDeleteBookPickById(BookPickId);
    }

}