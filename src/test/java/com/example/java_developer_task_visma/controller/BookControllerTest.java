package com.example.java_developer_task_visma.controller;

import com.example.java_developer_task_visma.Exceptions.BookNotFoundException;
import com.example.java_developer_task_visma.Exceptions.CantTakeBookThatLongException;
import com.example.java_developer_task_visma.Exceptions.ReaderHasReachedMaximumBooksThreshold;
import com.example.java_developer_task_visma.model.BookModel;
import com.example.java_developer_task_visma.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    private static final String READER_NAME = "Reader1";
    private static final String BOOK_NAME = "Book1";
    private static final String DATE_TAKEN_UNTIL = "2000-01-01";
    private static final String BOOK_GUID = "GUID1";
    private static final BookModel bookModel = new BookModel(READER_NAME, "Test1", "Cat",
            "Lang", "Date", "ISBN", BOOK_GUID, null, true);


    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /library/getBookByGUID/Test1")
    void getBookByGUID() throws Exception {

        doReturn(bookModel).when(bookService).findBookByGUID(BOOK_GUID);

        mockMvc.perform(get("/library/getBookByGUID/{GUID}", BOOK_GUID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.guid", is(BOOK_GUID)));
    }

    @Test
    @DisplayName("GET /library/getBookByGUID/Test1 Book not found")
    void getBookByGUIDShouldReturnBadRequest() throws Exception {

        doReturn(null).when(bookService).findBookByGUID(BOOK_GUID);

        mockMvc.perform(get("/library/getBookByGUID/{GUID}", BOOK_GUID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /library/getBooksByFilter Any String value")
    void getAllBooksByFilterAuthor() throws Exception {

        doReturn(Collections.singletonList(bookModel)).when(bookService).findAllBooksByFilter("author", "Test1");

        mockMvc.perform(get("/library/getBooksByFilter")
                        .param("filter", "author")
                        .param("value", "Test1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].author", is("Test1")));
    }

    @Test
    @DisplayName("GET /library/getBooksByFilter Taken value True")
    void getAllBooksByFilterBooleanTaken() throws Exception {

        doReturn(Collections.singletonList(bookModel)).when(bookService).findAllBooksByFilter("author", "Test1");

        mockMvc.perform(get("/library/getBooksByFilter")
                        .param("filter", "author")
                        .param("value", "Test1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].taken", is(true)));
    }

    @Test
    @DisplayName("GET /library/getBooksByFilter BookList value null")
    void getAllBooksByFilterShouldReturnBadRequestResponse() throws Exception {

        doReturn(null).when(bookService).findAllBooksByFilter("author", "Test1");

        mockMvc.perform(get("/library/getBooksByFilter")
                        .param("filter", "author")
                        .param("value", "Test1"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("POST /library/addBook Add new book to library")
    void addBookToLibrary() throws Exception {
        mockMvc.perform(post("/library/addBook")
                        .param("name", "Name1")
                        .param("author", "Author1")
                        .param("category", "Cat1")
                        .param("language", "Lang1")
                        .param("publicationDate", "2000-01-01")
                        .param("ISBN", "ISBN1")
                        .param("GUID", "GUID1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Name1")))
                .andExpect(jsonPath("$.author", is("Author1")))
                .andExpect(jsonPath("$.category", is("Cat1")))
                .andExpect(jsonPath("$.language", is("Lang1")))
                .andExpect(jsonPath("$.publicationDate", is("2000-01-01")))
                .andExpect(jsonPath("$.isbn", is("ISBN1")))
                .andExpect(jsonPath("$.guid", is("GUID1")))
                .andExpect(jsonPath("$.reader", nullValue()))
                .andExpect(jsonPath("$.taken", is(false)))
                .andExpect(jsonPath("$.returnDate", nullValue()));
    }

    @Test
    @DisplayName("POST /library/addBook Fail to add new book when param missing (ISBN)")
    void addBookToLibraryFailWhenParamMissing() throws Exception {
        mockMvc.perform(post("/library/addBook")
                        .param("name", "Name1")
                        .param("author", "Author1")
                        .param("category", "Cat1")
                        .param("language", "Lang1")
                        .param("publicationDate", "2000-01-01")
                        .param("GUID", "GUID1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /library/takeBook when changed taken to true successfully")
    void takeBookFromLibraryByReader() throws Exception {

        doNothing().when(bookService).takeBookFromLibraryByReader(READER_NAME, BOOK_NAME, DATE_TAKEN_UNTIL);

        mockMvc.perform(put("/library/takeBook")
                        .param("readerName", READER_NAME)
                        .param("takeUntilDate", DATE_TAKEN_UNTIL)
                        .param("bookName", BOOK_NAME))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /library/takeBook Fail when reader book threshold of 3 books reached")
    void takeBookFromLibraryByReaderFailWhenReaderReachedMaxBooks() throws Exception {

        doThrow(ReaderHasReachedMaximumBooksThreshold.class).when(bookService).takeBookFromLibraryByReader(READER_NAME, BOOK_NAME, DATE_TAKEN_UNTIL);

        mockMvc.perform(put("/library/takeBook")
                        .param("readerName", READER_NAME)
                        .param("takeUntilDate", DATE_TAKEN_UNTIL)
                        .param("bookName", BOOK_NAME))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /library/takeBook Fail when book not found")
    void takeBookFromLibraryByReaderFailWhenBookNotFound() throws Exception {

        doThrow(BookNotFoundException.class).when(bookService).takeBookFromLibraryByReader(READER_NAME, BOOK_NAME, DATE_TAKEN_UNTIL);

        mockMvc.perform(put("/library/takeBook")
                        .param("readerName", READER_NAME)
                        .param("takeUntilDate", DATE_TAKEN_UNTIL)
                        .param("bookName", BOOK_NAME))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /library/takeBook Fail when book is being taken for too long")
    void takeBookFromLibraryByReaderFailWhenBookTakenForTooLong() throws Exception {

        doThrow(CantTakeBookThatLongException.class).when(bookService).takeBookFromLibraryByReader(READER_NAME, BOOK_NAME, DATE_TAKEN_UNTIL);

        mockMvc.perform(put("/library/takeBook")
                        .param("readerName", READER_NAME)
                        .param("takeUntilDate", DATE_TAKEN_UNTIL)
                        .param("bookName", BOOK_NAME))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /library/deleteBookByGUID/{GUID} Remove book from library if found by GUID")
    void deleteBookByGUID() throws Exception {

        doReturn(bookModel).when(bookService).removeBookByGUID(BOOK_GUID);

        mockMvc.perform(delete("/library/deleteBookByGUID/{GUID}", BOOK_GUID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.guid", is(BOOK_GUID)));

    }

    @Test
    @DisplayName("DELETE /library/deleteBookByGUID/{GUID} Fail when book not found")
    void deleteBookByGUIDFailWhenBookNotFound() throws Exception {

        doThrow(BookNotFoundException.class).when(bookService).removeBookByGUID(BOOK_GUID);

        mockMvc.perform(delete("/library/deleteBookByGUID/{GUID}", BOOK_GUID))
                .andExpect(status().isBadRequest());
    }
}
