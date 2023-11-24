package me.jack.lat.lmsbackendmongo.service.oracleDB;

import me.jack.lat.lmsbackendmongo.model.NewBookAuthor;
import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

    public HashMap<String, Object>[] getBooksFromAuthor(Integer authorId) {
        ArrayList<HashMap<String, Object>> books = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT b.*,"
                    + "a.authorFirstName, a.authorLastName, "
                    + "c.categoryName, c.categoryDescription, "
                    + " (SELECT COUNT(*) FROM loanedBooks l WHERE l.bookId = b.id AND l.returnedAt IS NULL) AS loanedQuantity "
                    + " FROM BOOKS b "
                    + " INNER JOIN bookAuthors a ON b.bookAuthorId = a.ID "
                    + " INNER JOIN bookCategories c ON b.bookCategoryId = c.ID "
                    + " WHERE bookAuthorId = ?");

            preparedStatement.setInt(1, authorId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    HashMap<String, Object> book = new HashMap<>(){{
                        put("book", new HashMap<>(){{
                            put("bookId", resultSet.getInt("id"));
                            put("bookName", resultSet.getString("bookName"));
                            put("bookISBN", resultSet.getString("bookISBN"));
                            put("bookDescription", resultSet.getString("bookDescription"));
                            put("bookQuantity", resultSet.getInt("bookQuantity"));
                            put("bookThumbnailURL", resultSet.getString("bookThumbnailURL"));
                            put("bookPublishedDate", resultSet.getDate("bookPublishedDate"));
                            put("bookAuthorId", resultSet.getInt("bookAuthorId"));
                            put("bookCategory", new HashMap<>(){{
                                put("bookCategoryId", resultSet.getInt("bookCategoryId"));
                                put("categoryName", resultSet.getString("categoryName"));
                                put("categoryDescription", resultSet.getString("categoryDescription"));
                            }});
                            put("bookAuthor", new HashMap<>(){{
                                put("bookAuthorId", resultSet.getInt("bookAuthorId"));
                                put("authorFirstName", resultSet.getString("authorFirstName"));
                                put("authorLastName", resultSet.getString("authorLastName"));
                            }});
                        }});
                        put("booksLoaned", resultSet.getInt("loanedQuantity"));
                    }};

                    books.add(book);
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting books from author: " + e.getMessage());
            return null;
        }

        return books.toArray(new HashMap[0]);
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

    public HashMap<String, Object>[] getAuthors() {
        ArrayList<HashMap<String, Object>> authors = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM BOOKAUTHORS ORDER BY id");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    HashMap<String, Object> author = new HashMap<>();
                    author.put("bookAuthorId", resultSet.getInt("id"));
                    author.put("authorFirstName", resultSet.getString("authorFirstName"));
                    author.put("authorLastName", resultSet.getString("authorLastName"));
                    authors.add(author);
                }
            }

        } catch (Exception e) {
            logger.warning("Failed getting authors: " + e.getMessage());
            return null;
        }

        return authors.toArray(new HashMap[0]);
    }
}
