package com.example.java_developer_task_visma.controller;

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

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /library/getBookByGUID/1")
    void getBookByGUID() throws Exception {
        //Mocked service:
        BookModel bookModel = new BookModel("Name", "Author", "Cat", "Lang", "Date", "ISBN", "1", null, false);
        doReturn(bookModel).when(bookService).findBookByGUID("1");

        //Execute the GET request when book found:
        mockMvc.perform(get("/library/getBookByGUID/{GUID}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect( jsonPath("$.guid", is("1")));
    }

    @Test
    @DisplayName("GET /library/getBookByGUID/1 Book not found")
    void getBookByGUIDshouldReturnBadRequest() throws Exception {
        //Mocked service:
        doReturn(null).when(bookService).findBookByGUID("1");

        //Execute the GET request when book is null:
        mockMvc.perform(get("/library/getBookByGUID/{GUID}", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /getBooksByFilter BadRequest")
    void getAllBooksByFilter() {

    }

    @Test
    void addBookToLibrary() {

    }

    @Test
    void takeBookFromLibraryByReader() {

    }

    @Test
    void deleteBookByGUID() {

    }
}
