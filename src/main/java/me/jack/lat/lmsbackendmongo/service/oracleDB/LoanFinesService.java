package me.jack.lat.lmsbackendmongo.service.oracleDB;

import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

public class LoanFinesService {

    private static final Logger logger = Logger.getLogger(LoanFinesService.class.getName());

    public LoanFinesService() {

    }

    public HashMap<String, Object>[] findFinesForUser(int userId) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lf.*, "
                    + " lb.id as loanId, lb.userId as userId, lb.bookId as bookId, lb.loanedAt as loanedAt, lb.returnedAt as returnedAt, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription "
                    + " FROM loanedBooks lb "
                    + " INNER JOIN books b on b.id = lb.bookId "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " LEFT JOIN loanFines lf on lb.id = lf.loanId "
                    + " WHERE userId = ? AND lf.id IS NOT NULL");

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    loanedBooks.add(new HashMap<>() {{
                        put("loanedBookId", resultSet.getInt("loanId"));
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
                        put("loanFine", new HashMap<>() {{
                            put("loanFineId", resultSet.getInt("id"));
                            put("fineAmount", resultSet.getDouble("fineAmount"));
                            put("paidAt", resultSet.getDate("paidAt"));
                        }});
                    }});
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting loanFines for userId: " +  e.getMessage());
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public HashMap<String, Object>[] findFinesPaidForUser(int userId) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lf.*, "
                    + " lb.id as loanId, lb.userId as userId, lb.bookId as bookId, lb.loanedAt as loanedAt, lb.returnedAt as returnedAt, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription "
                    + " FROM loanedBooks lb "
                    + " INNER JOIN books b on b.id = lb.bookId "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " LEFT JOIN loanFines lf on lb.id = lf.loanId "
                    + " WHERE userId = ? AND lf.id IS NOT NULL AND lf.paidAt IS NOT NULL");

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanedBooks.add(new HashMap<>() {{
                        put("loanedBookId", resultSet.getInt("loanId"));
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
                        put("loanFine", new HashMap<>() {{
                            put("loanFineId", resultSet.getInt("id"));
                            put("fineAmount", resultSet.getDouble("fineAmount"));
                            put("paidAt", resultSet.getDate("paidAt"));
                        }});
                    }});
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting loanFines for userId: " +  e.getMessage());
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public HashMap<String, Object>[] findFinesUnpaidForUser(int userId) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lf.*, "
                    + " lb.id as loanId, lb.userId as userId, lb.bookId as bookId, lb.loanedAt as loanedAt, lb.returnedAt as returnedAt, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription "
                    + " FROM loanedBooks lb "
                    + " INNER JOIN books b on b.id = lb.bookId "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " LEFT JOIN loanFines lf on lb.id = lf.loanId "
                    + " WHERE userId = ? AND lf.id IS NOT NULL AND lf.paidAt IS NULL");

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanedBooks.add(new HashMap<>() {{
                        put("loanedBookId", resultSet.getInt("loanId"));
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
                        put("loanFine", new HashMap<>() {{
                            put("loanFineId", resultSet.getInt("id"));
                            put("fineAmount", resultSet.getDouble("fineAmount"));
                            put("paidAt", resultSet.getDate("paidAt"));
                        }});
                    }});
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting loanFines for userId: " +  e.getMessage());
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public HashMap<String, Object>[] findPaidFinesForUserBetweenDate(int userId, Date startDate, Date endDate) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lf.*, "
                    + " lb.id as loanId, lb.userId as userId, lb.bookId as bookId, lb.loanedAt as loanedAt, lb.returnedAt as returnedAt, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription "
                    + " FROM loanedBooks lb "
                    + " INNER JOIN books b on b.id = lb.bookId "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " LEFT JOIN loanFines lf on lb.id = lf.loanId "
                    + " WHERE userId = ? AND lf.id IS NOT NULL AND lf.fineAmount IS NOT NULL AND lf.paidAt BETWEEN ? AND ?");

            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, new java.sql.Date(startDate.getTime()));
            preparedStatement.setDate(3, new java.sql.Date(endDate.getTime()));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    if (resultSet.getInt("id") != 0) {
                        loanedBooks.add(new HashMap<>() {{
                            put("loanedBookId", resultSet.getInt("loanId"));
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
                            put("loanFine", new HashMap<>() {{
                                put("loanFineId", resultSet.getInt("id"));
                                put("fineAmount", resultSet.getDouble("fineAmount"));
                                put("paidAt", resultSet.getDate("paidAt"));
                            }});
                        }});
                    }
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting loanFines for userId: " +  e.getMessage());
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public HashMap<String, Object>[] findFinesForUserBetweenDate(int userId, Date startDate, Date endDate) {
        ArrayList<HashMap<String, Object>> loanedBooks = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lf.*, "
                    + " lb.id as loanId, lb.userId as userId, lb.bookId as bookId, lb.loanedAt as loanedAt, lb.returnedAt as returnedAt, "
                    + " b.bookName, b.bookISBN, b.bookISBN, b.bookDescription, b.bookQuantity, b.bookThumbnailURL, b.bookPublishedDate, "
                    + " ba.authorFirstName, ba.authorLastName, "
                    + " bc.categoryName, bc.categoryDescription "
                    + " FROM loanedBooks lb "
                    + " INNER JOIN books b on b.id = lb.bookId "
                    + " INNER JOIN bookAuthors ba on b.bookAuthorId = ba.id "
                    + " INNER JOIN bookCategories bc on b.bookCategoryId = bc.id "
                    + " LEFT JOIN loanFines lf on lb.id = lf.loanId "
                    + " WHERE userId = ? AND lf.id IS NOT NULL AND lb.returnedAt BETWEEN ? AND ?");

            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, new java.sql.Date(startDate.getTime()));
            preparedStatement.setDate(3, new java.sql.Date(endDate.getTime()));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    if (resultSet.getInt("id") != 0) {

                        loanedBooks.add(new HashMap<>() {{
                            put("loanedBookId", resultSet.getInt("loanId"));
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
                            put("loanFine", new HashMap<>() {{
                                put("loanFineId", resultSet.getInt("id"));
                                put("fineAmount", resultSet.getDouble("fineAmount"));
                                put("paidAt", resultSet.getDate("paidAt"));
                            }});
                        }});
                    }
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting loanFines for userId: " +  e.getMessage());
        }

        return loanedBooks.toArray(new HashMap[0]);
    }

    public Error payFine(Integer loanFineId, Integer userId) {

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE loanFines lf "
                    + " SET paidAt = ? "
                    + " WHERE lf.id = ? AND EXISTS "
                    + " (SELECT 1 FROM loanedBooks lb WHERE lb.id = lf.loanId AND lb.userId = ?) ");

            preparedStatement.setDate(1, new java.sql.Date(new Date().getTime()));
            preparedStatement.setInt(2, loanFineId);
            preparedStatement.setInt(3, userId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                return new Error("Failed paying fine - loanFine does not exist or does not belong to user");
            }

        } catch (SQLException e) {
            logger.warning("Failed paying fine for loanFineId: " + e.getMessage());
            return new Error("Failed paying fine");
        }

        return null;
    }
}
