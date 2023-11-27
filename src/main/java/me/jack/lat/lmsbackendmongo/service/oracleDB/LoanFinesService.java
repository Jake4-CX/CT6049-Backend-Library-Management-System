package me.jack.lat.lmsbackendmongo.service.oracleDB;

import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

public class LoanFinesService {

    private static final Logger logger = Logger.getLogger(LoanFinesService.class.getName());

    public LoanFinesService() {

    }

    public HashMap<String, Object>[] findFinesForUser(int userId) {
        ArrayList<HashMap<String, Object>> loanFines = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lf.* "
                    + " FROM loanedBooks lb "
                    + " LEFT JOIN loanFines lf on lb.id = lf.loanId "
                    + " WHERE userId = ? AND lf.id IS NOT NULL");

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanFines.add(new HashMap<>() {{
                        put("id", resultSet.getInt("id"));
                        put("loanId", resultSet.getInt("loanId"));
                        put("amountPaid", resultSet.getDouble("amountPaid"));
                        put("paidAt", resultSet.getDate("paidAt"));
                    }});
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting loanFines for userId: " +  e.getMessage());
        }

        return loanFines.toArray(new HashMap[0]);
    }

    public Integer totalFinePaidAmountForUser(int userId) {
        int totalFinePaidAmount = 0;

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(lf.amountPaid) as totalFinePaidAmount "
                    + " FROM loanedBooks lb "
                    + " LEFT JOIN loanFines lf on lb.id = lf.loanId "
                    + " WHERE userId = ? AND lf.id IS NOT NULL");

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    totalFinePaidAmount = resultSet.getInt("totalFinePaidAmount");
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting totalFinePaidAmount for userId: " +  e.getMessage());
        }

        return totalFinePaidAmount;
    }

    public HashMap<String, Object>[] findFinesForUserBetweenDate(int userId, Date startDate, Date endDate) {
        ArrayList<HashMap<String, Object>> loanFines = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT lf.* "
                    + " FROM loanedBooks lb "
                    + " LEFT JOIN loanFines lf on lb.id = lf.loanId "
                    + " WHERE userId = ? AND lf.id IS NOT NULL AND lf.paidAt BETWEEN ? AND ?");

            preparedStatement.setInt(1, userId);
            preparedStatement.setDate(2, new java.sql.Date(startDate.getTime()));
            preparedStatement.setDate(3, new java.sql.Date(endDate.getTime()));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    loanFines.add(new HashMap<>() {{
                        put("id", resultSet.getInt("id"));
                        put("loanId", resultSet.getInt("loanId"));
                        put("amountPaid", resultSet.getDouble("amountPaid"));
                        put("paidAt", resultSet.getDate("paidAt"));
                    }});
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting loanFines for userId: " +  e.getMessage());
        }

        return loanFines.toArray(new HashMap[0]);
    }
}
