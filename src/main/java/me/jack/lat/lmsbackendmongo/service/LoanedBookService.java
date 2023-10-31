package me.jack.lat.lmsbackendmongo.service;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.LoanedBook;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class LoanedBookService {

    private final Datastore datastore;

    private static final Logger logger = Logger.getLogger(LoanedBookService.class.getName());

    public LoanedBookService() {
        this.datastore = MongoDBUtil.getMongoDatastore();
    }

    /**
     * Get all loaned books (regardless of returned status), with BookId.
     *
     * @param bookId    bookId to search for
     */

    public List<LoanedBook> getLoanedBooksWithBookId(String bookId) {
        ObjectId objectId = new ObjectId(bookId);

        return datastore.find(LoanedBook.class)
                .filter(Filters.eq("_id", objectId))
                .iterator()
                .toList();
    }

    /**
     * Get all loaned books, that have not been returned, with BookId.
     *
     * @param bookId    bookId to search for
     */
    public List<LoanedBook> getUnreturnedBooksWithBookId(String bookId) {
        ObjectId objectId = new ObjectId(bookId);

        return datastore.find(LoanedBook.class)
                .filter(
                        Filters.eq("_id", objectId),
                        Filters.eq("returnedAt", null)
                )
                .iterator()
                .toList();
    }

//    /**
//     * Get all loaned books, that have been returned, with BookId.
//     *
//     * @param bookId    bookId to search for
//     */
//    public List<LoanedBook> getReturnedBooksWithBookId(String bookId) {
//        ObjectId objectId = new ObjectId(bookId);
//
//
//        // Todo: Put function here
//    }


    /**
     * Create a new LoanedBook.
     *
     * @param book    book to relate against
     * @param user    user to relate against
     *
     */
    public boolean createLoanedBook(Book book, User user) {
        LoanedBook loanedBook = new LoanedBook(book, user);

        if (isDuplicateLoanedBook(book, user)) {
            // User is already borrowing this book.
            return false;
        }

        try {
            datastore.save(loanedBook);
        } catch (Exception e) {
            logger.warning("Failed saving loanedBook: " + e.getMessage());
            return false;
        }

        return true;
    }


    /**
     * Check to see if the user already has an unreturned loan for the given book.
     *
     * @param book  book to relate against
     * @param user  user to relate against
     *
     * @return boolean
     */
    public boolean isDuplicateLoanedBook(Book book, User user) {
        return datastore.find(LoanedBook.class).filter(
                Filters.eq("book.bookId", new ObjectId(book.getBookId())),
                Filters.eq("user.userId", new ObjectId(user.getUserId()))
        ).count() > 0;
    }

}
