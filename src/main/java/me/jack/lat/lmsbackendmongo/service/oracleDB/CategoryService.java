package me.jack.lat.lmsbackendmongo.service.oracleDB;

import me.jack.lat.lmsbackendmongo.model.NewBookCategory;
import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class CategoryService {

    private static final Logger logger = Logger.getLogger(CategoryService.class.getName());

    public CategoryService() {
    }

    public HashMap<String, Object> getCategoryFromId(Integer authorId) {

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM BOOKCATEGORIES WHERE id = ?");
            preparedStatement.setInt(1, authorId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new HashMap<>(){{
                   put("categoryId", resultSet.getInt("id"));
                   put("categoryName", resultSet.getString("categoryName"));
                   put("categoryDescription", resultSet.getString("categoryDescription"));
                }};
            }

        } catch (Exception e) {
            logger.warning("Failed getting category from id: " + e.getMessage());
            return null;
        }

        return null;
    }

    public HashMap<String, Object>[] getBooksFromCategory(Integer categoryId) {
        ArrayList<HashMap<String, Object>> books = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT b.*,"
                    + "a.authorFirstName, a.authorLastName, "
                    + "c.categoryName, c.categoryDescription, "
                    + " (SELECT COUNT(*) FROM loanedBooks l WHERE l.bookId = b.id AND l.returnedAt IS NULL) AS loanedQuantity "
                    + " FROM BOOKS b "
                    + " INNER JOIN bookAuthors a ON b.bookAuthorId = a.ID "
                    + " INNER JOIN bookCategories c ON b.bookCategoryId = c.ID "
                    + " WHERE bookCategoryId = ?");

            preparedStatement.setInt(1, categoryId);

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
            logger.warning("Failed getting books from category: " + e.getMessage());
            return null;
        }

        return books.toArray(new HashMap[0]);
    }

    public Error createCategory(NewBookCategory newBookCategory) {

        if (isDuplicateCategory(newBookCategory)) {
            return new Error("Category with the same name already exists.");
        }

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO BOOKCATEGORIES (categoryName, categoryDescription) VALUES (?, ?)");
            preparedStatement.setString(1, newBookCategory.getCategoryName());
            preparedStatement.setString(2, newBookCategory.getCategoryDescription());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                return null;
            } else {
                return new Error("Failed creating new category");
            }

        } catch (Exception e) {
            logger.warning("Failed creating new category: " + e.getMessage());
            return new Error("Failed creating new category");
        }
    }

    public Error deleteCategory(Integer categoryId) {
        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE BOOKCATEGORIES SET ISDELETED = 1 WHERE id = ?");
            preparedStatement.setInt(1, categoryId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                return null;
            } else {
                return new Error("Failed deleting category");
            }

        } catch (Exception e) {
            logger.warning("Failed deleting category: " + e.getMessage());
            return new Error("Failed deleting category");
        }
    }

    public Error recoverCategory(Integer categoryId) {
        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE BOOKCATEGORIES SET ISDELETED = 0 WHERE id = ?");
            preparedStatement.setInt(1, categoryId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                return null;
            } else {
                return new Error("Failed recovering category");
            }

        } catch (Exception e) {
            logger.warning("Failed recovering category: " + e.getMessage());
            return new Error("Failed recovering category");
        }
    }

    private boolean isDuplicateCategory(NewBookCategory newBookCategory) {
        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT categoryName FROM BOOKCATEGORIES WHERE categoryName = ? AND ISDELETED = 0");
            preparedStatement.setString(1, newBookCategory.getCategoryName());

            return preparedStatement.executeQuery().next();

        } catch (Exception e) {
            logger.warning("Failed checking for duplicate category name: " + e.getMessage());
            return true;
        }
    }

    public HashMap<String, Object>[] getCategories() {
        ArrayList<HashMap<String, Object>> categories = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM BOOKCATEGORIES ORDER BY id AND ISDELETED = 0");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    HashMap<String, Object> category = new HashMap<>();
                    category.put("bookCategoryId", resultSet.getInt("id"));
                    category.put("categoryName", resultSet.getString("categoryName"));
                    category.put("categoryDescription", resultSet.getString("categoryDescription"));
                    categories.add(category);
                }
            }


        } catch (Exception e) {
            logger.warning("Failed getting categories: " + e.getMessage());
            return null;
        }

        return categories.toArray(new HashMap[0]);

    }

}
