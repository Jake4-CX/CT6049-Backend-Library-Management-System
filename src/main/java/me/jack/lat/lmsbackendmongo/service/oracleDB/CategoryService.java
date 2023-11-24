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

    private boolean isDuplicateCategory(NewBookCategory newBookCategory) {
        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT categoryName FROM BOOKCATEGORIES WHERE categoryName = ?");
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
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM BOOKCATEGORIES ORDER BY id");

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
