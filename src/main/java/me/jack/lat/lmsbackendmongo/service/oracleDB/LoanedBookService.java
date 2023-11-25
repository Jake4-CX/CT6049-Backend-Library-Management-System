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

    public Error returnAndPayFine(Integer loanId, Integer userId) {

        HashMap<String, Object> loanedBook = getLoanedBookFromId(loanId);
        if (loanedBook == null) {
            return new Error("No loaned book found with this id");
        }

        boolean isOverdue = isOverdue(loanedBook);

        try (Connection connection = OracleDBUtil.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE LOANEDBOOKS SET returnedAt = SYSDATE WHERE id = ? AND userId = ? AND returnedAt IS NULL");
            updateStatement.setInt(1, loanId);
            updateStatement.setInt(2, userId);
            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected == 0) {
                connection.rollback(); // Rollback if no rows are updated
                return new Error("No loaned book found with this id");
            }

            // handle the fine
            if (isOverdue) {
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO LOANFINES (loanId, amountPaid, paidAt) VALUES (?, ?, SYSDATE)");
                insertStatement.setInt(1, loanId);
                insertStatement.setInt(2, (int) (Double.parseDouble(dotenv.get("LATE_FINE_PER_DAY")) * getDaysOverdue(loanedBook)));
                insertStatement.executeUpdate();
            }

            // Commit transaction
            connection.commit();
        } catch (SQLException e) {
            logger.warning("Failed returning book and handling fine: " + e.getMessage());
            return new Error("Failed returning book and handling fine");
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
