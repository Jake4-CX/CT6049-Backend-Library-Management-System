package me.jack.lat.lmsbackendmongo.util;

import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class OracleDBUtil {

    private static final Dotenv dotenv = Dotenv.configure().load();
    private static final String ORACLE_URL = dotenv.get("ORACLE_DB_URL");
    private static final String ORACLE_USERNAME = dotenv.get("ORACLE_DB_USERNAME");
    private static final String ORACLE_PASSWORD = dotenv.get("ORACLE_DB_PASSWORD");

    private static final DataSource dataSource;


    static {

        BasicDataSource ds = new BasicDataSource();

        ds.setUrl(ORACLE_URL);
        ds.setUsername(ORACLE_USERNAME);
        ds.setPassword(ORACLE_PASSWORD);

        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);

        dataSource = ds;

    }

    public static Connection getConnection() throws SQLException {
            return dataSource.getConnection();
    }

}
