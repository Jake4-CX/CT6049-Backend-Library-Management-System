package me.jack.lat.lmsbackendmongo.service.oracleDB;

import io.github.cdimascio.dotenv.Dotenv;
import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class LoanedBookService {

    private static final Logger logger = Logger.getLogger(LoanedBookService.class.getName());
    private static final Dotenv dotenv = Dotenv.configure().load();

    public LoanedBookService() {

    }

    public HashMap<String, Object>[] getUnreturnedBooks(Integer userId) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM LOANEDBOOKS WHERE userId = ? AND returnedAt IS NULL");
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanedBooks.add(new HashMap<>(){{
                        put("bookId", resultSet.getString("bookId"));
                        put("userId", resultSet.getString("userId"));
                        put("loanedAt", resultSet.getDate("loanedAt"));
                        put("returnedAt", null);
                    }});
                }

            }

        } catch (Exception e) {
            logger.warning("Failed getting unreturned books" + e.getMessage());
            return null;
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public Error returnLoanedBook(Integer loanId, Integer userId) {

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE LOANEDBOOKS SET returnedAt = SYSDATE WHERE id = ? AND userId = ? AND returnedAt IS NULL");
            preparedStatement.setInt(1, loanId);
            preparedStatement.setInt(2, userId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                return new Error("No loaned book found with this id");
            }

        } catch (SQLException e) {
            logger.warning("Failed returning book" + e.getMessage());
            return new Error("Failed returning book");
        }

        return null;
    }

    public HashMap<String, Object> getLoanedBookFromId(Integer loanId) {
        HashMap<String, Object> loanedBook = new HashMap<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM LOANEDBOOKS WHERE id = ?");
            preparedStatement.setInt(1, loanId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    loanedBook.put("id", resultSet.getInt("id"));
                    loanedBook.put("bookId", resultSet.getInt("bookId"));
                    loanedBook.put("userId", resultSet.getInt("userId"));
                    loanedBook.put("loanedAt", resultSet.getDate("loanedAt"));
                    loanedBook.put("returnedAt", resultSet.getDate("returnedAt"));
                } else {
                    return null;
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting loaned book" + e.getMessage());
            return null;
        }

        return loanedBook;
    }

    public HashMap<String, Object>[] getLoanedBooksForUser(Integer userId) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lb.*, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription, "
                    + " u.userEmail, u.userRole, u.userCreatedDate, u.userUpdatedDate, "
                    + " lf.id AS loanFineId, lf.fineAmount, lf.paidAt "
                    + " FROM LOANEDBOOKS lb "
                    + " INNER JOIN books b on lb.bookId = b.id "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " INNER JOIN users u on lb.userId = u.id "
                    + " LEFT JOIN loanFines lf on lb.id = lf.loanId "
                    + " WHERE userId = ?");
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanedBooks.add(new HashMap<>(){{
                        put("loanedBookId", resultSet.getInt("id"));
                        put("loanedAt", resultSet.getDate("loanedAt"));
                        put("returnedAt", resultSet.getDate("returnedAt"));
                        put("book", new HashMap<>(){{
                            put("bookId", resultSet.getInt("bookId"));
                            put("bookName", resultSet.getString("bookName"));
                            put("bookISBN", resultSet.getString("bookISBN"));
                            put("bookDescription", resultSet.getString("bookDescription"));
                            put("bookQuantity", resultSet.getInt("bookQuantity"));
                            put("bookThumbnailURL", resultSet.getString("bookThumbnailURL"));
                            put("bookPublishedDate", resultSet.getDate("bookPublishedDate"));
                            put("bookAuthor", new HashMap<>(){{
                                put("authorFirstName", resultSet.getString("authorFirstName"));
                                put("authorLastName", resultSet.getString("authorLastName"));
                            }});
                            put("bookCategory", new HashMap<>(){{
                                put("categoryName", resultSet.getString("categoryName"));
                                put("categoryDescription", resultSet.getString("categoryDescription"));
                            }});
                        }});
                        put("user", new HashMap<>(){{
                            put("userId", resultSet.getInt("userId"));
                            put("userEmail", resultSet.getString("userEmail"));
                            put("userRole", resultSet.getString("userRole"));
                            put("userCreatedDate", resultSet.getDate("userCreatedDate"));
                            put("userUpdatedDate", resultSet.getDate("userUpdatedDate"));
                        }});

                        if (resultSet.getInt("loanFineId") != 0) {
                            put("loanFine", new HashMap<>() {{
                                put("loanFineId", resultSet.getInt("loanFineId"));
                                put("fineAmount", resultSet.getInt("fineAmount"));
                                put("paidAt", resultSet.getDate("paidAt"));
                            }});
                        }
                    }});
                }

            }

        } catch (Exception e) {
            logger.warning("Failed getting loaned books" + e.getMessage());
            return null;
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public HashMap<String, Object>[] getLoanedBooksForUserBetweenDate(Integer userId, Date startDate, Date endDate) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lb.*, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription, "
                    + " u.userEmail, u.userRole, u.userCreatedDate, u.userUpdatedDate, "
                    + " lf.id AS loanFineId, lf.fineAmount, lf.paidAt "
                    + " FROM LOANEDBOOKS lb "
                    + " INNER JOIN books b on lb.bookId = b.id "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " INNER JOIN users u on lb.userId = u.id "
                    + " LEFT JOIN loanFines lf on lb.id = lf.loanId "
                    + " WHERE userId = ? AND loanedAt BETWEEN ? AND ?");
            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, startDate);
            preparedStatement.setDate(3, endDate);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanedBooks.add(new HashMap<>(){{
                        put("loanedBookId", resultSet.getInt("id"));
                        put("loanedAt", resultSet.getDate("loanedAt"));
                        put("returnedAt", resultSet.getDate("returnedAt"));
                        put("book", new HashMap<>(){{
                            put("bookId", resultSet.getInt("bookId"));
                            put("bookName", resultSet.getString("bookName"));
                            put("bookISBN", resultSet.getString("bookISBN"));
                            put("bookDescription", resultSet.getString("bookDescription"));
                            put("bookQuantity", resultSet.getInt("bookQuantity"));
                            put("bookThumbnailURL", resultSet.getString("bookThumbnailURL"));
                            put("bookPublishedDate", resultSet.getDate("bookPublishedDate"));
                            put("bookAuthor", new HashMap<>(){{
                                put("authorFirstName", resultSet.getString("authorFirstName"));
                                put("authorLastName", resultSet.getString("authorLastName"));
                            }});
                            put("bookCategory", new HashMap<>(){{
                                put("categoryName", resultSet.getString("categoryName"));
                                put("categoryDescription", resultSet.getString("categoryDescription"));
                            }});
                        }});
                        put("user", new HashMap<>(){{
                            put("userId", resultSet.getInt("userId"));
                            put("userEmail", resultSet.getString("userEmail"));
                            put("userRole", resultSet.getString("userRole"));
                            put("userCreatedDate", resultSet.getDate("userCreatedDate"));
                            put("userUpdatedDate", resultSet.getDate("userUpdatedDate"));
                        }});

                        if (resultSet.getInt("loanFineId") != 0) {
                            put("loanFine", new HashMap<>() {{
                                put("loanFineId", resultSet.getInt("loanFineId"));
                                put("fineAmount", resultSet.getInt("fineAmount"));
                                put("paidAt", resultSet.getDate("paidAt"));
                            }});
                        }
                    }});
                }

            }

        } catch (Exception e) {
            logger.warning("Failed getting loaned books" + e.getMessage());
            return null;
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public HashMap<String, Object>[] findAllOverdueBooks() {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lb.*, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription, "
                    + " u.userEmail, u.userRole, u.userCreatedDate, u.userUpdatedDate "
                    + " FROM LOANEDBOOKS lb "
                    + " INNER JOIN books b on lb.bookId = b.id "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " INNER JOIN users u on lb.userId = u.id "
                    + " WHERE returnedAt IS NULL AND loanedAt < SYSDATE - 14");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanedBooks.add(new HashMap<>() {{
                        put("loanedBookId", resultSet.getInt("id"));
                        put("loanedAt", resultSet.getDate("loanedAt"));
                        put("returnedAt", resultSet.getDate("returnedAt"));
                        put("book", new HashMap<>() {{
                            put("bookId", resultSet.getInt("bookId"));
                            put("bookName", resultSet.getString("bookName"));
                            put("bookISBN", resultSet.getString("bookISBN"));
                            put("bookDescription", resultSet.getString("bookDescription"));
                            put("bookQuantity", resultSet.getInt("bookQuantity"));
                            put("bookThumbnailURL", resultSet.getString("bookThumbnailURL"));
                            put("bookPublishedDate", resultSet.getDate("bookPublishedDate"));
                            put("bookAuthor", new HashMap<>() {{
                                put("authorFirstName", resultSet.getString("authorFirstName"));
                                put("authorLastName", resultSet.getString("authorLastName"));
                            }});
                            put("bookCategory", new HashMap<>() {{
                                put("categoryName", resultSet.getString("categoryName"));
                                put("categoryDescription", resultSet.getString("categoryDescription"));
                            }});
                        }});
                        put("user", new HashMap<>() {{
                            put("userId", resultSet.getInt("userId"));
                            put("userEmail", resultSet.getString("userEmail"));
                            put("userRole", resultSet.getString("userRole"));
                            put("userCreatedDate", resultSet.getDate("userCreatedDate"));
                            put("userUpdatedDate", resultSet.getDate("userUpdatedDate"));
                        }});

                    }});
                }
            }
        } catch (Exception e) {
            logger.warning("Failed getting overdue books" + e.getMessage());
            return null;
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public HashMap<String, Object>[] findOverdueBooksForUser(Integer userId) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lb.*, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription "
                    + " FROM LOANEDBOOKS lb "
                    + " INNER JOIN books b on lb.bookId = b.id "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " WHERE returnedAt IS NULL AND loanedAt < SYSDATE - 14 AND userId = ?");
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanedBooks.add(new HashMap<>() {{
                        put("loanedBookId", resultSet.getInt("id"));
                        put("loanedAt", resultSet.getDate("loanedAt"));
                        put("returnedAt", resultSet.getDate("returnedAt"));
                        put("book", new HashMap<>() {{
                            put("bookId", resultSet.getInt("bookId"));
                            put("bookName", resultSet.getString("bookName"));
                            put("bookISBN", resultSet.getString("bookISBN"));
                            put("bookDescription", resultSet.getString("bookDescription"));
                            put("bookQuantity", resultSet.getInt("bookQuantity"));
                            put("bookThumbnailURL", resultSet.getString("bookThumbnailURL"));
                            put("bookPublishedDate", resultSet.getDate("bookPublishedDate"));
                            put("bookAuthor", new HashMap<>() {{
                                put("authorFirstName", resultSet.getString("authorFirstName"));
                                put("authorLastName", resultSet.getString("authorLastName"));
                            }});
                            put("bookCategory", new HashMap<>() {{
                                put("categoryName", resultSet.getString("categoryName"));
                                put("categoryDescription", resultSet.getString("categoryDescription"));
                            }});
                        }});
                    }});
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting overdue books" + e.getMessage());
            return null;
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public HashMap<String, Object>[] findHistoricLoanedBooksForUser(Integer userId) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lb.*, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription, "
                    + " lf.id AS loanFineId, lf.fineAmount, lf.paidAt "
                    + " FROM LOANEDBOOKS lb "
                    + " INNER JOIN books b on lb.bookId = b.id "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " LEFT JOIN loanFines lf on lb.id = lf.loanId "
                    + " WHERE returnedAt IS NOT NULL AND userId = ?");
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanedBooks.add(new HashMap<>() {{
                        put("loanedBookId", resultSet.getInt("id"));
                        put("loanedAt", resultSet.getDate("loanedAt"));
                        put("returnedAt", resultSet.getDate("returnedAt"));
                        put("book", new HashMap<>() {{
                            put("bookId", resultSet.getInt("bookId"));
                            put("bookName", resultSet.getString("bookName"));
                            put("bookISBN", resultSet.getString("bookISBN"));
                            put("bookDescription", resultSet.getString("bookDescription"));
                            put("bookQuantity", resultSet.getInt("bookQuantity"));
                            put("bookThumbnailURL", resultSet.getString("bookThumbnailURL"));
                            put("bookPublishedDate", resultSet.getDate("bookPublishedDate"));
                            put("bookAuthor", new HashMap<>() {{
                                put("authorFirstName", resultSet.getString("authorFirstName"));
                                put("authorLastName", resultSet.getString("authorLastName"));
                            }});
                            put("bookCategory", new HashMap<>() {{
                                put("categoryName", resultSet.getString("categoryName"));
                                put("categoryDescription", resultSet.getString("categoryDescription"));
                            }});
                        }});

                        if (resultSet.getInt("loanFineId") != 0) {
                            put("loanFine", new HashMap<>() {{
                                put("loanFineId", resultSet.getInt("loanFineId"));
                                put("fineAmount", resultSet.getInt("fineAmount"));
                                put("paidAt", resultSet.getDate("paidAt"));
                            }});
                        }
                    }});
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting overdue books" + e.getMessage());
            return null;
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public HashMap<String, Object>[] findCurrentBorrowedBooksForUser(Integer userId) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lb.*, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription "
                    + " FROM LOANEDBOOKS lb "
                    + " INNER JOIN books b on lb.bookId = b.id "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " WHERE returnedAt IS NULL AND loanedAt < SYSDATE - 14 AND userId = ?");
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanedBooks.add(new HashMap<>() {{
                        put("loanedBookId", resultSet.getInt("id"));
                        put("loanedAt", resultSet.getDate("loanedAt"));
                        put("returnedAt", resultSet.getDate("returnedAt"));
                        put("book", new HashMap<>() {{
                            put("bookId", resultSet.getInt("bookId"));
                            put("bookName", resultSet.getString("bookName"));
                            put("bookISBN", resultSet.getString("bookISBN"));
                            put("bookDescription", resultSet.getString("bookDescription"));
                            put("bookQuantity", resultSet.getInt("bookQuantity"));
                            put("bookThumbnailURL", resultSet.getString("bookThumbnailURL"));
                            put("bookPublishedDate", resultSet.getDate("bookPublishedDate"));
                            put("bookAuthor", new HashMap<>() {{
                                put("authorFirstName", resultSet.getString("authorFirstName"));
                                put("authorLastName", resultSet.getString("authorLastName"));
                            }});
                            put("bookCategory", new HashMap<>() {{
                                put("categoryName", resultSet.getString("categoryName"));
                                put("categoryDescription", resultSet.getString("categoryDescription"));
                            }});
                        }});
                    }});
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting overdue books" + e.getMessage());
            return null;
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public HashMap<String, Object>[] findCurrentLoansForUser(Integer userId) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lb.*, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription "
                    + " FROM LOANEDBOOKS lb "
                    + " INNER JOIN books b on lb.bookId = b.id "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " WHERE returnedAt IS NULL AND userId = ?");
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanedBooks.add(new HashMap<>() {{
                        put("loanedBookId", resultSet.getInt("id"));
                        put("loanedAt", resultSet.getDate("loanedAt"));
                        put("returnedAt", resultSet.getDate("returnedAt"));
                        put("book", new HashMap<>() {{
                            put("bookId", resultSet.getInt("bookId"));
                            put("bookName", resultSet.getString("bookName"));
                            put("bookISBN", resultSet.getString("bookISBN"));
                            put("bookDescription", resultSet.getString("bookDescription"));
                            put("bookQuantity", resultSet.getInt("bookQuantity"));
                            put("bookThumbnailURL", resultSet.getString("bookThumbnailURL"));
                            put("bookPublishedDate", resultSet.getDate("bookPublishedDate"));
                            put("bookAuthor", new HashMap<>() {{
                                put("authorFirstName", resultSet.getString("authorFirstName"));
                                put("authorLastName", resultSet.getString("authorLastName"));
                            }});
                            put("bookCategory", new HashMap<>() {{
                                put("categoryName", resultSet.getString("categoryName"));
                                put("categoryDescription", resultSet.getString("categoryDescription"));
                            }});
                        }});
                    }});

                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting overdue books" + e.getMessage());
            return null;
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public HashMap<String, Object>[] findAllBorrowedBooks() {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lb.*, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription, "
                    + " u.userEmail, u.userRole, u.userCreatedDate, u.userUpdatedDate "
                    + " FROM LOANEDBOOKS lb "
                    + " INNER JOIN books b on lb.bookId = b.id "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " INNER JOIN users u on lb.userId = u.id "
                    + " WHERE returnedAt IS NULL");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanedBooks.add(new HashMap<>() {{
                        put("loanedBookId", resultSet.getInt("id"));
                        put("loanedAt", resultSet.getDate("loanedAt"));
                        put("returnedAt", resultSet.getDate("returnedAt"));
                        put("book", new HashMap<>() {{
                            put("bookId", resultSet.getInt("bookId"));
                            put("bookName", resultSet.getString("bookName"));
                            put("bookISBN", resultSet.getString("bookISBN"));
                            put("bookDescription", resultSet.getString("bookDescription"));
                            put("bookQuantity", resultSet.getInt("bookQuantity"));
                            put("bookThumbnailURL", resultSet.getString("bookThumbnailURL"));
                            put("bookPublishedDate", resultSet.getDate("bookPublishedDate"));
                            put("bookAuthor", new HashMap<>() {{
                                put("authorFirstName", resultSet.getString("authorFirstName"));
                                put("authorLastName", resultSet.getString("authorLastName"));
                            }});
                            put("bookCategory", new HashMap<>() {{
                                put("categoryName", resultSet.getString("categoryName"));
                                put("categoryDescription", resultSet.getString("categoryDescription"));
                            }});
                        }});
                        put("user", new HashMap<>(){{
                            put("userId", resultSet.getInt("userId"));
                            put("userEmail", resultSet.getString("userEmail"));
                            put("userRole", resultSet.getString("userRole"));
                            put("userCreatedDate", resultSet.getDate("userCreatedDate"));
                            put("userUpdatedDate", resultSet.getDate("userUpdatedDate"));
                        }});

                    }});
                }
            }
        } catch (Exception e) {
            logger.warning("Failed getting overdue books" + e.getMessage());
            return null;
        }

        return loanedBooks.toArray(new HashMap[0]);
    }


    public boolean isOverdue(HashMap<String, Object> loanedBook) {
        Date loanedAt = (Date) loanedBook.get("loanedAt");

        return (loanedAt.before(getFourteenDaysAgo()));
    }

    private java.util.Date getFourteenDaysAgo() {
        final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
        return new java.util.Date(System.currentTimeMillis() - 14 * MILLIS_PER_DAY);
    }

    private Double getDaysOverdue(HashMap<String, Object> loanedBook) {
        java.util.Date fourteenDaysAgo = getFourteenDaysAgo();

        long diff = fourteenDaysAgo.getTime() - ((Date) loanedBook.get("loanedAt")).getTime();
        return (double) (diff / (1000 * 60 * 60 * 24));
    }
}
