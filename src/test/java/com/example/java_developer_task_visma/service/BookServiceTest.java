package com.example.java_developer_task_visma.service;

import com.example.java_developer_task_visma.Exceptions.BookNotFoundException;
import com.example.java_developer_task_visma.Exceptions.CantTakeBookThatLongException;
import com.example.java_developer_task_visma.Exceptions.ReaderHasReachedMaximumBooksThreshold;
import com.example.java_developer_task_visma.model.BookModel;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class BookServiceTest {

    private static final String BOOK_NAME = "Book1";
    private static final String BOOK_GUID = "GUID1";
    private static final String BOOK_GUID_TO_BE_TAKEN = "GUID_TAKEN";
    private static final String BOOK_NAME_TO_BE_TAKEN = "BOOK_NAME_TO_BE_TAKEN";
    private static final BookModel bookModel = new BookModel(BOOK_NAME, "Test1", "Cat",
            "Lang", "Date", "ISBN", BOOK_GUID, null, false);
    private static final BookModel bookModelToBeTaken = new BookModel(BOOK_NAME_TO_BE_TAKEN, "Test1", "Cat",
            "Lang", "Date", "ISBN", BOOK_GUID_TO_BE_TAKEN, null, false);

    private static final String READER_NAME = "Reader1";
    private static final String DATE_PLUS_DAY = LocalDate.now().plusDays(1).toString();

    @Autowired
    private BookService bookService;

    @BeforeEach
    void createTestBookJsonAtLibrary() {

    }

    @AfterEach
    void removeTestBookJsonAtLibrary() throws BookNotFoundException {

    }

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
    @DisplayName("Add book to library ")
    void addBookToLibrary() throws BookNotFoundException {
        bookService.addBookToLibrary(bookModel);

        BookModel returnedBook = bookService.findBookByGUID(BOOK_GUID);
        Assertions.assertEquals(bookModel.getGUID(), returnedBook.getGUID());

        bookService.removeBookByGUID(BOOK_GUID);
    }

    @Test
    @DisplayName("Take book from library by setting taken true")
    void takeBookFromLibraryByReader() throws ReaderHasReachedMaximumBooksThreshold, CantTakeBookThatLongException, BookNotFoundException {
        bookService.addBookToLibrary(bookModelToBeTaken);
        bookService.takeBookFromLibraryByReader(READER_NAME, BOOK_NAME_TO_BE_TAKEN, DATE_PLUS_DAY);

        List<BookModel> bookModelList = bookService.findAllBooksByFilter("taken", "true");
        BookModel bookModel = bookModelList.stream().filter(e -> e.getName().equals(bookModelToBeTaken.getName())).findAny().orElse(null);

        Assertions.assertNotNull(bookModel);
        Assertions.assertEquals(bookModelToBeTaken.getGUID(), bookModel.getGUID());

        bookService.removeBookWithTakenTrue(bookModel.getName());

    }

    @Test
    void removeBookByGUID() throws BookNotFoundException {
        bookService.addBookToLibrary(bookModel);

        BookModel returnedBook = bookService.findBookByGUID(BOOK_GUID);
        Assertions.assertEquals(bookModel.getGUID(), returnedBook.getGUID());

        bookService.removeBookByGUID(BOOK_GUID);

        returnedBook = bookService.findBookByGUID(BOOK_GUID);
        Assertions.assertNull(returnedBook);
    }

    @Test
    void findAllBooksByFilter() throws BookNotFoundException {
        bookService.addBookToLibrary(bookModel);
        List<BookModel> bookModelList = bookService.findAllBooksByFilter("ISBN", bookModel.getISBN());

        BookModel filteredBookByGUID = bookModelList.stream().filter(e-> e.getGUID().equals(bookModel.getGUID())).findAny().orElse(null);

        Assertions.assertEquals(filteredBookByGUID.getGUID(), bookModel.getGUID());

        bookService.removeBookByGUID(BOOK_GUID);

    }
}
