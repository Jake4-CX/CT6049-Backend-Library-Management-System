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
     * Get all loaned books (regardless of returned status), with Book.
     *
     * @param book    bookId to search for
     */

    public List<LoanedBook> getLoanedBooksWithBook(Book book) {

        return datastore.find(LoanedBook.class)
                .filter(
                        Filters.eq("book", book)
                )
                .iterator()
                .toList();
    }

    /**
     * Get all loaned books, that have not been returned, with Book.
     *
     * @param book    bookId to search for
     *
     * @return List<LoanedBook>
     */
    public List<LoanedBook> getUnreturnedBooksWithBook(Book book) {

        return datastore.find(LoanedBook.class)
                .filter(
                        Filters.eq("book", book),
                        Filters.eq("returnedAt", null)
                )
                .iterator()
                .toList();
    }

    /**
     * Get all loaned books, that have been returned, with Book.
     *
     * @param book    bookId to search for
     *
     * @return List<LoanedBook>
     */
    public List<LoanedBook> getReturnedBooksWithBook(Book book) {

        return datastore.find(LoanedBook.class)
                .filter(
                        Filters.eq("book", book),
                        Filters.exists("returnedAt")
                )
                .iterator()
                .toList();
    }


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
        List<LoanedBook> loanedBooks = datastore.find(LoanedBook.class)
                .filter(
                        Filters.eq("book", book),
                        Filters.eq("user", user),
                        Filters.eq("returnedAt", null)
                )
                .iterator()
                .toList();

        return !loanedBooks.isEmpty();
    }

    /**
     * Return all overdue books.
     *
     * @return List<LoanedBook>
     */

    public List<LoanedBook> findOverdueBooks() {
        Date fourteenDaysAgo = getFourteenDaysAgo();

        var query = datastore.find(LoanedBook.class)
                .filter(
                        Filters.and(
                                Filters.lt("loanedAt", fourteenDaysAgo),
                                Filters.eq("returnedAt", null)
                        )
                );

        return query.iterator().toList();
    }

    private Date getFourteenDaysAgo() {
        final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
        return new Date(System.currentTimeMillis() - 14 * MILLIS_PER_DAY);
    }

}
