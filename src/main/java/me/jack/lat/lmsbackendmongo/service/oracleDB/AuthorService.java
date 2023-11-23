package me.jack.lat.lmsbackendmongo.service.oracleDB;

import me.jack.lat.lmsbackendmongo.model.NewBookAuthor;
import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Logger;

public class AuthorService {

    private static final Logger logger = Logger.getLogger(AuthorService.class.getName());

    public AuthorService() {
    }

    public HashMap<String, Object> getAuthorFromId(Integer authorId) {

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM BOOKAUTHORS WHERE id = ?");
            preparedStatement.setInt(1, authorId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new HashMap<>(){{
                    put("authorId", resultSet.getInt("id"));
                    put("authorFirstName", resultSet.getString("authorFirstName"));
                    put("authorLastName", resultSet.getString("authorLastName"));
                }};
            }

        } catch (Exception e) {
            logger.warning("Failed getting author from id: " + e.getMessage());
            return null;
        }

        return null;
    }

    public Error createAuthor(NewBookAuthor newAuthor) {

        if (isDuplicateAuthor(newAuthor)) {
            return new Error("Author with the same name already exists.");
        }

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO BOOKAUTHORS (authorFirstName, authorLastName) VALUES (?, ?)");
            preparedStatement.setString(1, newAuthor.getAuthorFirstName());
            preparedStatement.setString(2, newAuthor.getAuthorLastName());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                return null;
            } else {
                return new Error("Failed creating new author");
            }

        } catch (Exception e) {
            logger.warning("Failed saving author: " + e.getMessage());
            return new Error("Failed creating new author");
        }

    }

    private boolean isDuplicateAuthor(NewBookAuthor newAuthor) {
        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT authorFirstName, authorLastName FROM BOOKAUTHORS WHERE authorFirstName = ? AND authorLastName = ?");
            preparedStatement.setString(1, newAuthor.getAuthorFirstName());
            preparedStatement.setString(2, newAuthor.getAuthorLastName());

            return preparedStatement.executeQuery().next();

        } catch (Exception e) {
            logger.warning("Failed checking for duplicate author name: " + e.getMessage());
            return true;
        }

    }
}