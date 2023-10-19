package me.jack.lat.lmsbackendmongo.service;


import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.filters.Filters;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.BookAuthor;
import me.jack.lat.lmsbackendmongo.model.NewBook;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;

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

        // ToDo: Get bookAuthor and bookCategory from database

        AuthorService authorService = new AuthorService();

        BookAuthor bookAuthor = authorService.getAuthorFromId(newBook.getBookAuthorId());

        logger.info(("bookAuthor id: '" + newBook.getBookAuthorId() + "'"));

        if (bookAuthor == null) {
            // ToDo: Return error message - author not found
            logger.info("bookAuthor is null");
            return false;
        }

        Book book = new Book(
                newBook.getBookName(),
                newBook.getBookISBN(),
                newBook.getBookDescription(),
                newBook.getBookQuantity(),
                new Date(),
                null,
                bookAuthor
        );

        try {
            datastore.save(book);
        } catch (Exception e) {
            logger.severe("Failed saving book: " + e.getMessage());
            return false;
        }

        return true;
    }

    private boolean isDuplicateBookName(String bookName) {
        return datastore.find(Book.class).filter(Filters.eq("bookName", bookName)).count() > 0;

    }
}
