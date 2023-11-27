package me.jack.lat.lmsbackendmongo.service.mongoDB;


import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.filters.Filters;
import me.jack.lat.lmsbackendmongo.entities.*;
import me.jack.lat.lmsbackendmongo.model.NewBook;
import me.jack.lat.lmsbackendmongo.util.DateUtil;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;
import org.bson.types.ObjectId;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class BookService {

    private final Datastore datastore;
    private static final Logger logger = Logger.getLogger(BookService.class.getName());

    public BookService() {
        this.datastore = MongoDBUtil.getMongoDatastore();
    }

    public List<Book> getBooks(String sort, String filter, int page, int limit) {
        FindOptions findOptions = new FindOptions().skip((page - 1) * limit).limit(limit);

        return datastore.find(Book.class).iterator(findOptions).toList();
    }

    public boolean createBook(NewBook newBook) {

        if (isDuplicateBookName(newBook.getBookName())) {
            return false;
        }

        AuthorService authorService = new AuthorService();
        CategoryService categoryService = new CategoryService();

        BookAuthor bookAuthor = authorService.getAuthorFromId(newBook.getBookAuthorId());

        logger.info(("bookAuthor id: '" + newBook.getBookAuthorId() + "'"));

        if (bookAuthor == null) {
            // ToDo: Return error message - author not found
            logger.info("bookAuthor is null");
            return false;
        }

        BookCategory bookCategory = categoryService.getCategoryFromId(newBook.getBookCategoryId());

        logger.info(("bookCategory id: '" + newBook.getBookCategoryId() + "'"));

        if (bookCategory == null) {
            //ToDo: Return error message - category not found
            logger.info("bookCategory is null");
            return false;
        }

        try {

            Date bookPublishedDate = DateUtil.convertStringToDate(newBook.getBookPublishedDate());

            Book book = new Book(
                    newBook.getBookName(),
                    newBook.getBookISBN(),
                    newBook.getBookDescription(),
                    newBook.getBookQuantity(),
                    bookPublishedDate,
                    newBook.getBookThumbnailURL(),
                    bookCategory,
                    bookAuthor
            );

            try {
                datastore.save(book);
            } catch (Exception e) {
                logger.warning("Failed saving book: " + e.getMessage());
                return false;
            }

        } catch (ParseException e) {
            logger.warning("Failed converting bookPublishedDate: " + e.getMessage());
            return false;
        }

        return true;
    }

    private boolean isDuplicateBookName(String bookName) {
        return datastore.find(Book.class).filter(Filters.eq("bookName", bookName)).count() > 0;

    }

    public Book getBookFromId(String bookId) {
        ObjectId objectId = new ObjectId(bookId);

        return datastore.find(Book.class).filter(Filters.eq("_id", objectId)).first();
    }

    public Book getBookFromName(String bookName) {
        return datastore.find(Book.class).filter(Filters.eq("bookName", bookName)).first();
    }

    public List<Book> searchBooks(String searchQuery) {

        return datastore.find(Book.class)
                .filter(Filters.regex("bookName")
                        .pattern(searchQuery)
                        .caseInsensitive()
                ).iterator()
                .toList();
    }

    public Error borrowBook(String bookId, User user) {
        Book requestedBook = getBookFromId(bookId);

        if (requestedBook == null) {
            // A Book does not exist with this Id
            return new Error("Book does not exist with this Id.");
        }

        LoanedBookService loanedBookService = new LoanedBookService();
        List<LoanedBook> loanedBooks = loanedBookService.getUnreturnedBooksWithBook(requestedBook);

        if (loanedBooks.size() >= requestedBook.getBookQuantity()) {
            // All books borrowed. Deny user's request
            return new Error("There are no books available to borrow. Please try again later");
        }

        return loanedBookService.createLoanedBook(requestedBook, user) ? null : new Error("You are already borrowing this book.");

    }

}
