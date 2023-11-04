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
     * Get loaned book from id.
     *
     * @param loanedBookId  loanedBookId to search for
     *
     * @return LoanedBook
     */
    public LoanedBook getLoanedBookFromId(String loanedBookId) {
        try {
            return datastore.find(LoanedBook.class)
                    .filter(
                            Filters.eq("_id", new ObjectId(loanedBookId))
                    )
                    .first();
        } catch (Exception e) {
            logger.warning("Failed to get loanedBook from id: " + e.getMessage());
            return null;
        }
    }

    /**
     * Update loaned book.
     *
     * @param loanedBook loanedBook to update
     *
     * @return boolean
     */
    public boolean updateLoanedBook(LoanedBook loanedBook) {
        try {
            datastore.save(loanedBook);
        } catch (Exception e) {
            logger.warning("Failed to update loanedBook: " + e.getMessage());
            return false;
        }

        return true;
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
     * Check to see if the loanedBook is overdue.
     *
     * @param loanedBook  loanedBook to check
     *
     * @return boolean
     */
    public boolean isOverdue(LoanedBook loanedBook) {
        Date fourteenDaysAgo = getFourteenDaysAgo();

        return loanedBook.getLoanedAt().before(fourteenDaysAgo);
    }

    /**
     * Return all overdue books.
     *
     * @return List<LoanedBook>
     */

    public List<LoanedBook> findAllOverdueBooks() {
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

    /**
     * Return all borrowed books.
     *
     * @return List<LoanedBook>
     */
    public List<LoanedBook> findAllBorrowedBooks() {
        Date fourteenDaysAgo = getFourteenDaysAgo();

        var query = datastore.find(LoanedBook.class)
                .filter(
                        Filters.and(
                                Filters.gte("loanedAt", fourteenDaysAgo),
                                Filters.eq("returnedAt", null)
                        )
                );

        return query.iterator().toList();
    }

    /**
     * Return all overdue books for a given user.
     *
     * @param user  user to relate against
     *
     * @return List<LoanedBook>
     */
    public List<LoanedBook> findOverdueBooksForUser(User user) {
        Date fourteenDaysAgo = getFourteenDaysAgo();

        var query = datastore.find(LoanedBook.class)
                .filter(
                        Filters.and(
                                Filters.eq("user", user),
                                Filters.lt("loanedAt", fourteenDaysAgo),
                                Filters.eq("returnedAt", null)
                        )
                );

        return query.iterator().toList();
    }

    /**
     * Return all overdue books for a given book.
     *
     * @param book  book to relate against
     *
     * @return List<LoanedBook>
     */
    public List<LoanedBook> findOverdueBooksForBook(Book book) {
        Date fourteenDaysAgo = getFourteenDaysAgo();

        var query = datastore.find(LoanedBook.class)
                .filter(
                        Filters.and(
                                Filters.eq("book", book),
                                Filters.lt("loanedAt", fourteenDaysAgo),
                                Filters.eq("returnedAt", null)
                        )
                );

        return query.iterator().toList();
    }

    /**
     * Return all current loans for a given user.
     *
     * @param user  user to relate against
     *
     * @return List<LoanedBook>
     */
    public List<LoanedBook> findCurrentLoansForUser(User user) {
        var query = datastore.find(LoanedBook.class)
                .filter(
                        Filters.and(
                                Filters.eq("user", user),
                                Filters.eq("returnedAt", null)
                        )
                );

        return query.iterator().toList();
    }

    /**
     * Return all current borrowed (not overdue) books for a given user.
     *
     * @param user  user to relate against
     *
     * @return List<LoanedBook>
     */
    public List<LoanedBook> findCurrentBorrowedBooksForUser(User user) {
        Date fourteenDaysAgo = getFourteenDaysAgo();

        var query = datastore.find(LoanedBook.class)
                .filter(
                        Filters.and(
                                Filters.eq("user", user),
                                Filters.gte("loanedAt", fourteenDaysAgo),
                                Filters.eq("returnedAt", null)
                        )
                );

        return query.iterator().toList();
    }

    /**
     * Return all loaned books for a given user. (regardless of returned status)
     *
     * @param user  user to relate against
     *
     * @return List<LoanedBook>
     */
    public List<LoanedBook> findLoanedBooksForUser(User user) {
        var query = datastore.find(LoanedBook.class)
                .filter(
                        Filters.eq("user", user)
                );

        return query.iterator().toList();
    }

    /**
     * Return historic loaned books for a given user.
     *
     * @param user  user to relate against
     *
     * @return List<LoanedBook>
     */
    public List<LoanedBook> findHistoricLoanedBooksForUser(User user) {
        var query = datastore.find(LoanedBook.class)
                .filter(
                        Filters.and(
                                Filters.eq("user", user),
                                Filters.exists("returnedAt")
                        )
                );

        return query.iterator().toList();
    }


    private Date getFourteenDaysAgo() {
        final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
        return new Date(System.currentTimeMillis() - 14 * MILLIS_PER_DAY);
    }

}
