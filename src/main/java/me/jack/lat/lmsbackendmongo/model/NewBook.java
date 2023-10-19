package me.jack.lat.lmsbackendmongo.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NewBook {

    @NotNull
    @Size(min = 2, max = 50)
    String bookName;
    @NotNull
    Integer bookISBN;
    @NotEmpty
    String bookDescription;
    @NotNull
    Integer bookQuantity;

    @NotNull
    String bookCategoryId;

    @NotNull
    String bookAuthorId;

    public String getBookName() {
        return bookName;
    }

    public Integer getBookISBN() {
        return bookISBN;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public Integer getBookQuantity() {
        return bookQuantity;
    }

    public String getBookCategoryId() {
        return bookCategoryId;
    }

    public String getBookAuthorId() {
        return bookAuthorId;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setBookISBN(Integer bookISBN) {
        this.bookISBN = bookISBN;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public void setBookQuantity(Integer bookQuantity) {
        this.bookQuantity = bookQuantity;
    }

    public void setBookCategoryId(String bookCategoryId) {
        this.bookCategoryId = bookCategoryId;
    }

    public void setBookAuthorId(String bookAuthorId) {
        this.bookAuthorId = bookAuthorId;
    }
}
