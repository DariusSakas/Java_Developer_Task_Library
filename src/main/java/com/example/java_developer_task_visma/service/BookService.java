package com.example.java_developer_task_visma.service;

import com.example.java_developer_task_visma.Exceptions.BookNotFoundException;
import com.example.java_developer_task_visma.Exceptions.CantTakeBookThatLongException;
import com.example.java_developer_task_visma.Exceptions.ReaderHasReachedMaximumBooksThreshold;
import com.example.java_developer_task_visma.model.BookModel;
import com.example.java_developer_task_visma.model.ReaderModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class BookService {

    private static final String LIBRARY_PATH_FROM_CONTENT_ROOT = "src/main/resources/library.json";

    private final ObjectMapper objectMapper;

    public BookService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public BookModel findBookByGUID(String guid) {
        List<BookModel> allRegisteredBooks = getAllBooksFromLibrary();
        return allRegisteredBooks.stream().filter(e -> e.getGUID().equals(guid)).findAny().orElse(null);
    }

    public void addBookToLibrary(BookModel newBookToAdd) {
        List<BookModel> bookModelList = getAllBooksFromLibrary();
        bookModelList.add(newBookToAdd);

        saveModifiedBookListToLibrary(bookModelList);
    }

    public void takeBookFromLibraryByReader(String readerName, String bookName, String takeUntilDate) throws BookNotFoundException, ReaderHasReachedMaximumBooksThreshold, CantTakeBookThatLongException {

        List<BookModel> allRegisteredBooks = getAllBooksFromLibrary();
        List<BookModel> booksThatArentTaken = collectBooksToListThatHaveTakenFalse(allRegisteredBooks);

        checkIfReaderIsCapableOfTakingOneMoreBook(readerName, allRegisteredBooks);
        checkIfTakeUntilDateIsValid(takeUntilDate);

        BookModel bookToTake = findBookByName(booksThatArentTaken, bookName);
        throwExceptionIfBookToTakeIsNull(bookToTake);

        setBookValueTakenTrue(bookToTake);
        setTakeUntilDate(takeUntilDate, bookToTake);
        setReaderName(readerName, bookToTake);

        putTakenBookToLibraryList(allRegisteredBooks, bookToTake);

    }

    public void removeBookByGUID(String GUID) {
        List<BookModel> allRegisteredBooks = getAllBooksFromLibrary();
        List<BookModel> booksThatArentTaken = collectBooksToListThatHaveTakenFalse(allRegisteredBooks);

        booksThatArentTaken.stream().filter(e -> e.getGUID().equals(GUID)).findAny().ifPresent(allRegisteredBooks::remove);

        saveModifiedBookListToLibrary(allRegisteredBooks);
        System.out.println("Book removed");
    }

    private void saveModifiedBookListToLibrary(List<BookModel> bookModelList) {
        try {
            objectMapper.writeValue(new File(LIBRARY_PATH_FROM_CONTENT_ROOT), bookModelList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<BookModel> getAllBooksFromLibrary() {
        List<BookModel> bookModelList = new ArrayList<>();
        try {
            System.out.println("Reading values");
            bookModelList = objectMapper.readValue(new File(LIBRARY_PATH_FROM_CONTENT_ROOT), new TypeReference<List<BookModel>>() {
            });
            System.out.println("Successfully read");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Reading error");
        }
        return bookModelList;
    }

    private void setReaderName(String readerName, BookModel bookToTake) {
        bookToTake.setReader(new ReaderModel(readerName));
    }

    private void setTakeUntilDate(String takeUntilDate, BookModel bookToTake) {
        bookToTake.setReturnDate(takeUntilDate);
    }

    private void checkIfTakeUntilDateIsValid(String takeUntilDate) throws CantTakeBookThatLongException {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date date = dateFormat.parse(takeUntilDate);
            String localDate = LocalDate.now().toString();
            Date dateNow = dateFormat.parse(localDate);

            long timeInMillis = Math.abs(date.getTime() - dateNow.getTime());
            long timeDifference = TimeUnit.DAYS.convert(timeInMillis, TimeUnit.MILLISECONDS);

            if (timeDifference > 60) {
                throw new CantTakeBookThatLongException("Book take until date shouldn't be longer than 60 days.");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void checkIfReaderIsCapableOfTakingOneMoreBook(String readerName, List<BookModel> allRegisteredBooks) throws ReaderHasReachedMaximumBooksThreshold {

        long readersCount = allRegisteredBooks.stream().filter(e -> e.getReader() != null).filter(e -> e.getReader().getName().equals(readerName)).count();

        if (readersCount >= 3) {
            throw new ReaderHasReachedMaximumBooksThreshold("User already has 3 books. Please bring them back before taking another");
        }
    }

    private void putTakenBookToLibraryList(List<BookModel> allRegisteredBooks, BookModel bookToTake) {
        allRegisteredBooks.removeIf(book -> book.getName().equals(bookToTake.getName())
                && book.getISBN().equals(bookToTake.getISBN())
                && book.getGUID().equals(bookToTake.getGUID()));
        allRegisteredBooks.add(bookToTake);
        saveModifiedBookListToLibrary(allRegisteredBooks);
    }

    private void setBookValueTakenTrue(BookModel bookToTake) {
        bookToTake.setTaken(true);
    }

    private void throwExceptionIfBookToTakeIsNull(BookModel bookToTake) throws BookNotFoundException {
        if (bookToTake == null)
            throw new BookNotFoundException("Such a book is not found in library");
    }

    private BookModel findBookByName(List<BookModel> booksThatArentTaken, String bookName) {
        return booksThatArentTaken.stream().filter(e -> e.getName().equals(bookName)).findAny().orElse(null);
    }

    private List<BookModel> collectBooksToListThatHaveTakenFalse(List<BookModel> allRegisteredBooks) {
        return allRegisteredBooks.stream().filter(e -> !e.getTaken()).collect(Collectors.toList());
    }


    public List<BookModel> findAllBooksByFilter(String filter, String value) {
        List<BookModel> allBooks = getAllBooksFromLibrary();
        switch (filter) {
            case ("name"):
                return allBooks.stream().filter(e-> e.getName().equals(value)).collect(Collectors.toList());
            case ("author"):
                return allBooks.stream().filter(e->e.getAuthor().equals(value)).collect(Collectors.toList());
            case ("category"):
                return allBooks.stream().filter(e-> e.getCategory().equals(value)).collect(Collectors.toList());
            case ("language"):
                return allBooks.stream().filter(e->e.getLanguage().equals(value)).collect(Collectors.toList());
            case ("ISBN"):
                return allBooks.stream().filter(e-> e.getISBN().equals(value)).collect(Collectors.toList());
            case("taken"):
                return allBooks.stream().filter(e-> e.getTaken().toString().equals(value)).collect(Collectors.toList());

        }
        return null;
    }
}
