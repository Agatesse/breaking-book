package com.projects.breakingbook.persistence.entity;

public enum BookStatus {
    UNREAD("UNREAD"),
    ONGOING("ONGOING"),
    READ("READ");

    private String bookStatusString;

    BookStatus(String bookStatusString) {
        this.bookStatusString = bookStatusString;
    }

    public String getBookStatusString() {
        return bookStatusString;
    }
}
