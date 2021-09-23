package com.example.java_developer_task_visma.controller;

import com.example.java_developer_task_visma.Exceptions.BookNotFoundException;
import com.example.java_developer_task_visma.Exceptions.CantTakeBookThatLongException;
import com.example.java_developer_task_visma.Exceptions.ReaderHasReachedMaximumBooksThreshold;
import com.example.java_developer_task_visma.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.java_developer_task_visma.model.BookModel;

import java.util.List;

@RestController
@RequestMapping("/library")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/getBookByGUID/{GUID}")
    public ResponseEntity<?> getBookByGUID(@PathVariable String GUID) {
        BookModel bookModel = bookService.findBookByGUID(GUID);

        if (bookModel == null) {
            return new ResponseEntity<>("Book not found. Check GUID", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(bookModel, HttpStatus.OK);
    }

    @GetMapping("/getBooksByFilter")
    public ResponseEntity<?> getAllBooksByFilter(
            @RequestParam(value = "filter") String filter,
            @RequestParam(value = "value") String value
    ) {
        List<BookModel> bookList = bookService.findAllBooksByFilter(filter, value);
        if (bookList == null){
            return new ResponseEntity<>("Book not found. Check filter or value", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(bookList, HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping("/addBook")
    public ResponseEntity<String> addBookToLibrary(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "author") String author,
            @RequestParam(value = "category") String category,
            @RequestParam(value = "language") String language,
            @RequestParam(value = "publicationDate") String publicationDate,
            @RequestParam(value = "ISBN") String ISBN,
            @RequestParam(value = "GUID") String GUID
    ) {
        BookModel newBookToAdd = new BookModel();

        newBookToAdd.setName(name);
        newBookToAdd.setAuthor(author);
        newBookToAdd.setCategory(category);
        newBookToAdd.setLanguage(language);
        newBookToAdd.setPublicationDate(publicationDate);
        newBookToAdd.setISBN(ISBN);
        newBookToAdd.setGUID(GUID);
        newBookToAdd.setTaken(false);

        bookService.addBookToLibrary(newBookToAdd);

        return new ResponseEntity<>("Book successfully added to library.JSON", HttpStatus.OK);
    }


    @PutMapping
    @RequestMapping("/takeBook")
    public ResponseEntity<String> takeBookFromLibraryByReader(
            @RequestParam(value = "readerName") String readerName,
            @RequestParam(value = "takeUntilDate") String takeUntilDate,
            @RequestParam(value = "bookName") String bookName
    ) {
        try {
            bookService.takeBookFromLibraryByReader(readerName, bookName, takeUntilDate);
            return new ResponseEntity<>("Book successfully taken from library", HttpStatus.OK);
        } catch (BookNotFoundException | ReaderHasReachedMaximumBooksThreshold | CantTakeBookThatLongException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Can't find book or reader already has 3 books or date value is incorrect", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/deleteBookByGUID/{GUID}")
    public ResponseEntity<String> deleteBookByGUID(@PathVariable String GUID) {

        bookService.removeBookByGUID(GUID);
        return new ResponseEntity<>("Book removed succesfully", HttpStatus.OK);
    }

}
