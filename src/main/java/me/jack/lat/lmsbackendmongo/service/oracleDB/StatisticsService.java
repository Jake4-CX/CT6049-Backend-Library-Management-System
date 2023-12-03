package me.jack.lat.lmsbackendmongo.service.oracleDB;

import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class StatisticsService {

    private static final Logger logger = Logger.getLogger(StatisticsService.class.getName());

    public StatisticsService() {
    }

    public HashMap<String, Object> getAdminStatisticsOverview() {

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT"
                    + " (SELECT COUNT(*) FROM books) AS totalBooks, "
                    + " (SELECT COUNT(*) FROM users) AS totalUsers, "
                    + " (SELECT COUNT(*) FROM loanedBooks WHERE returnedAt IS NULL) AS totalIssuedBooks,"
                    + " (SELECT COUNT(*) FROM loanedBooks WHERE returnedAt IS NULL AND TRUNC(SYSDATE) - TRUNC(LOANEDAT) >= 14) AS totalOverdueBooks "
                    + " FROM dual");

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new HashMap<>(){{
                    put("totalBooks", resultSet.getInt("totalBooks"));
                    put("totalUsers", resultSet.getInt("totalUsers"));
                    put("totalIssuedBooks", resultSet.getInt("totalIssuedBooks"));
                    put("totalOverdueBooks", resultSet.getInt("totalOverdueBooks"));
                }};

            }

        } catch (Exception e) {
            logger.severe("Failed to get admin statistics overview: " + e.getMessage());
        }

        return null;
    }

    public HashMap<String, Object> getAdminBookCirculationStatistics() {

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT "
                    + " (SELECT COUNT(*) FROM loanedBooks WHERE returnedAt IS NULL) AS totalIssuedBooks, "
                    + " (SELECT COUNT(*) FROM loanedBooks WHERE returnedAt IS NULL AND TRUNC(SYSDATE) - TRUNC(LOANEDAT) >= 14) AS totalOverdueBooks, "
                    + " (SELECT COUNT(*) FROM loanedBooks WHERE returnedAt IS NULL AND TRUNC(SYSDATE) - TRUNC(LOANEDAT) < 14) AS totalNotOverdueBooks "
                    + " FROM dual");

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new HashMap<>(){{
                    put("totalIssuedBooks", resultSet.getInt("totalIssuedBooks"));
                    put("totalOverdueBooks", resultSet.getInt("totalOverdueBooks"));
                    put("totalNotOverdueBooks", resultSet.getInt("totalNotOverdueBooks"));
                }};
            }
        } catch (Exception e) {
            logger.severe("Failed to get admin book circulation statistics: " + e.getMessage());
        }

        return null;
    }

    public HashMap<String, Object>[] getAdminBookCategoryStatistics() {
        List<HashMap<String, Object>> categoryCounts = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT "
            + " bc.categoryName, COUNT(b.id) AS bookCount"
            + " FROM books b "
            + " JOIN BOOKCATEGORIES bc ON b.bookCategoryId = bc.id "
            + " GROUP BY bc.categoryName");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                categoryCounts.add(new HashMap<>(){{
                    put("categoryName", resultSet.getString("categoryName"));
                    put("bookCount", resultSet.getInt("bookCount"));
                }});
            }

        } catch (Exception e) {
            logger.severe("Failed to get admin book category statistics: " + e.getMessage());
        }

        return categoryCounts.toArray(new HashMap[0]);
    }
}
