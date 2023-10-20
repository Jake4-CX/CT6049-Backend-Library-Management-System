package me.jack.lat.lmsbackendmongo.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NewBook {

    @NotNull(message = "Book name cannot be null")
    @Size(min = 2, max = 50, message = "Book name must be between 2 and 50 characters")
    String bookName;

    @Digits(integer = 13, fraction = 0, message = "Book ISBN must be a number")
    Integer bookISBN;
    @NotEmpty(message = "Book description cannot be empty")
    String bookDescription;

    @Digits(integer = 9, fraction = 0, message = "Book quantity must be a number")
    Integer bookQuantity;

    @NotNull(message = "Book category ID cannot be null")
    String bookCategoryId;

    @NotNull(message = "Book author ID cannot be null")
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
