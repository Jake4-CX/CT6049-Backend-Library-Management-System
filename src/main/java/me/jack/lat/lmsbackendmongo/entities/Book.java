package me.jack.lat.lmsbackendmongo.entities;

import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity("books")
public class Book {

    @Id
    private ObjectId bookId;
    @Indexed(options = @IndexOptions(unique = true))
    private String bookName;
    @Indexed(options = @IndexOptions(unique = true))
    private Integer bookISBN;
    private String bookDescription;
    private Integer bookQuantity;
    private Date bookPublishedDate;

    private String bookThumbnailURL;

    @Reference
    private BookCategory bookCategory;

    @Reference
    private BookAuthor bookAuthor;

    public Book() {
        this.bookPublishedDate = new Date();
    }

    public Book(String bookName, Integer bookISBN, String bookDescription, Integer bookQuantity, Date bookPublishedDate, String bookThumbnailURL, BookCategory bookCategory, BookAuthor bookAuthor) {
        this.bookName = bookName;
        this.bookISBN = bookISBN;
        this.bookDescription = bookDescription;

        this.bookQuantity = bookQuantity;
        this.bookPublishedDate = bookPublishedDate;

        this.bookCategory = bookCategory;
        this.bookAuthor = bookAuthor;
    }

    public String getBookId() {
        return bookId.toString();
    }

    public void setBookId(String bookId) {
        this.bookId = new ObjectId(bookId);
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

    public String getBookThumbnailURL() {
        return bookThumbnailURL;
    }

    public void setBookThumbnailURL(String bookThumbnailURL) {
        this.bookThumbnailURL = bookThumbnailURL;
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
