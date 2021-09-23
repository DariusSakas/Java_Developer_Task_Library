package com.example.java_developer_task_visma.service;

import com.example.java_developer_task_visma.Exceptions.BookNotFoundException;
import com.example.java_developer_task_visma.model.BookModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class BookServiceTest {

    private static final String READER_NAME = "Reader1";
    private static final String BOOK_GUID = "GUID1";
    private static final BookModel bookModel = new BookModel(READER_NAME, "Test1", "Cat",
            "Lang", "Date", "ISBN", BOOK_GUID, null, false);

    private static final String BOOK_NAME = "Book1";
    private static final String DATE_PLUS_DAY = LocalDate.now().plusDays(1).toString();

    @Autowired
    private  BookService bookService;

    @Test
    @DisplayName("Find book by GUID ")
    void findBookByGUID() throws BookNotFoundException {
        bookService.addBookToLibrary(bookModel);

        BookModel returnedBook = bookService.findBookByGUID(BOOK_GUID);
        Assertions.assertEquals(bookModel.getGUID(), returnedBook.getGUID());

        bookService.removeBookByGUID(BOOK_GUID);
    }

    @Test
    @DisplayName("Find book should return null when no book found ")
    void findBookByGUIDShouldReturnNullWhenNotfound() {

        BookModel returnedBook = bookService.findBookByGUID(BOOK_GUID);
        Assertions.assertNull(returnedBook);
    }

    @Test
    void addBookToLibrary() {

    }

    @Test
    void takeBookFromLibraryByReader() {

    }

    @Test
    void removeBookByGUID() {

    }

    @Test
    void findAllBooksByFilter() {

    }
}
