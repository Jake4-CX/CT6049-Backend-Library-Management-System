package me.jack.lat.lmsbackendmongo.service.oracleDB;

import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
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
}
