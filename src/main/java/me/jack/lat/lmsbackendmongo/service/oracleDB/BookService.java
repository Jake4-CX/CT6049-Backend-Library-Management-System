package me.jack.lat.lmsbackendmongo.service.oracleDB;

import me.jack.lat.lmsbackendmongo.model.NewBook;
import me.jack.lat.lmsbackendmongo.util.DateUtil;
import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.logging.Logger;

public class BookService {

    private static final Logger logger = Logger.getLogger(BookService.class.getName());

    public BookService() {
    }

    public Error createBook(NewBook newBook) {

        if (isDuplicateBookName(newBook.getBookName())) {
            return new Error("Book with the same name already exists.");
        }

        AuthorService authorService = new AuthorService();
        CategoryService categoryService = new CategoryService();

        HashMap<String, Object> bookAuthor = authorService.getAuthorFromId(Integer.valueOf(newBook.getBookAuthorId()));

        logger.info(("bookAuthor id: '" + newBook.getBookAuthorId() + "'"));

        if (bookAuthor == null) {
            return new Error("Author not found");
        }

        HashMap<String, Object> bookCategory = categoryService.getCategoryFromId(Integer.valueOf(newBook.getBookCategoryId()));

        logger.info(("bookCategory id: '" + newBook.getBookCategoryId() + "'"));

        if (bookCategory == null) {
            return new Error("Category not found");
        }

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO BOOKS (bookName, bookISBN, bookDescription, bookQuantity, bookThumbnailURL, bookPublishedDate, bookAuthorId, bookCategoryId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, newBook.getBookName());
            preparedStatement.setString(2, String.valueOf(newBook.getBookISBN()));
            preparedStatement.setString(3, newBook.getBookDescription());
            preparedStatement.setInt(4, newBook.getBookQuantity());
            preparedStatement.setString(5, newBook.getBookThumbnailURL());
            preparedStatement.setDate(6, new Date(DateUtil.convertStringToDate((newBook.getBookPublishedDate())).getTime()));
            preparedStatement.setInt(7, Integer.parseInt(newBook.getBookAuthorId()));
            preparedStatement.setInt(8, Integer.parseInt(newBook.getBookCategoryId()));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                return null;
            } else {
                return new Error("Failed creating new book");
            }

        } catch (Exception e) {
            return new Error("Failed creating new book");
        }

    }

    private boolean isDuplicateBookName(String bookName) {
        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT bookName FROM BOOKS WHERE BOOKNAME = ?");
            preparedStatement.setString(1, bookName);

            return preparedStatement.executeQuery().next();

        } catch (Exception e) {
            logger.warning("Failed checking for duplicate book name: " + e.getMessage());
            return true;
        }

    }
}
