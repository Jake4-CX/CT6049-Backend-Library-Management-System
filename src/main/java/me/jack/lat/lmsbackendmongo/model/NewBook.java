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
    Integer bookCategoryId;

    @NotNull
    Integer bookAuthorId;

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

    public Integer getBookCategoryId() {
        return bookCategoryId;
    }

    public Integer getBookAuthorId() {
        return bookAuthorId;
    }
}
