package com.example.phoenix.library;

public class book {
    private String title;
    private String author;
    private String theme;
    private int bookCount;
    private String description;
    private String ID;
    public book(String title,String author,String theme, int bookCount, String description,String ID){
        this.title = title;
        this.author = author;
        this.theme = theme;
        this.bookCount = bookCount;
        this.description = description;
        this.ID = ID;
    }
    public String getID() {
        return ID;
    }

    public String getAuthor() {
        return author;
    }

    public String getTheme() {
        return theme;
    }

    public String getTitle() {
        return title;
    }

    public int getBookCount() {
        return bookCount;
    }

    public String getDescription() {
        return description;
    }
}
