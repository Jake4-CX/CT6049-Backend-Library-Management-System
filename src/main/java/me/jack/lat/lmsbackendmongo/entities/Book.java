package me.jack.lat.lmsbackendmongo.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Date;

@Entity("books")
public class Book {

    @Id
    private ObjectId bookId;

    @NotNull
    @Size(min = 2, max = 50)
    private String bookName;
    @NotNull
    private Integer bookISBN;
    @NotEmpty
    private String bookDescription;
    @NotNull
    private Integer bookQuantity;
    private Date bookPublishedDate;

    @NotNull
    @Reference
    private BookCategory bookCategory;

    @NotNull
    @Reference
    private BookAuthor bookAuthor;

    public Book() {
        this.bookPublishedDate = new Date();
    }

    public Book(String bookName, Integer bookISBN, String bookDescription, Integer bookQuantity, Date bookPublishedDate, BookCategory bookCategory, BookAuthor bookAuthor) {
        this.bookName = bookName;
        this.bookISBN = bookISBN;
        this.bookDescription = bookDescription;

        this.bookQuantity = bookQuantity;
        this.bookPublishedDate = bookPublishedDate;

        this.bookCategory = bookCategory;
        this.bookAuthor = bookAuthor;
    }

    public ObjectId getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Integer getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(Integer bookISBN) {
        this.bookISBN = bookISBN;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public Integer getBookQuantity() {
        return bookQuantity;
    }

    public void setBookQuantity(Integer bookQuantity) {
        this.bookQuantity = bookQuantity;
    }

    public Date getBookPublishedDate() {
        return bookPublishedDate;
    }

    public void setBookPublishedDate(Date bookPublishedDate) {
        this.bookPublishedDate = bookPublishedDate;
    }

    public BookCategory getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(BookCategory bookCategory) {
        this.bookCategory = bookCategory;
    }

    public BookAuthor getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(BookAuthor bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
}
