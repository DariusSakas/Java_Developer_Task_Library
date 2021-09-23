package com.example.java_developer_task_visma.model;

public class BookModel {

    private String name;
    private String author;
    private String category;
    private String language;
    private String publicationDate;
    private String ISBN;
    private String GUID;

    private Boolean taken;
    private ReaderModel reader;
    private String returnDate;

    public BookModel() {
    }

    public BookModel(String name, String author, String category, String language, String publicationDate, String ISBN, String GUID, ReaderModel reader, Boolean taken, String returnDate) {
        this.name = name;
        this.author = author;
        this.category = category;
        this.language = language;
        this.publicationDate = publicationDate;
        this.ISBN = ISBN;
        this.GUID = GUID;
        this.reader = reader;
        this.taken = taken;
        this.returnDate = returnDate;
    }

    public BookModel(String name, String author, String category, String language, String publicationDate, String ISBN, String GUID, ReaderModel reader, Boolean taken) {
        this.name = name;
        this.author = author;
        this.category = category;
        this.language = language;
        this.publicationDate = publicationDate;
        this.ISBN = ISBN;
        this.GUID = GUID;
        this.reader = reader;
        this.taken = taken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public ReaderModel getReader() {
        return reader;
    }

    public void setReader(ReaderModel reader) {
        this.reader = reader;
    }

    public Boolean getTaken() {
        return taken;
    }

    public void setTaken(Boolean taken) {
        this.taken = taken;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
