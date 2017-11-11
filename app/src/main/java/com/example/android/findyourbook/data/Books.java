package com.example.android.findyourbook.data;

public class Books {

    private String mBookTitle;
    private String mBookAuthor;
    private String mBookPublisher;
    private String mPublishDate;
    private String mBookUrl;

    public Books(String bookTitle, String bookAuthor, String bookPublisher, String publishedDate, String bookUrl) {
        mBookTitle = bookTitle;
        mBookAuthor = bookAuthor;
        mBookPublisher = bookPublisher;
        mPublishDate = publishedDate;
        mBookUrl = bookUrl;
    }

    public String getBookTitle() {
        return mBookTitle;
    }

    public String getBookAuthor() {
        return mBookAuthor;
    }

    public String getBookPublisher() {
        return mBookPublisher;
    }

    public String getPublishDate() {
        return mPublishDate;
    }

    public String getBookUrl() {
        return mBookUrl;
    }

}
