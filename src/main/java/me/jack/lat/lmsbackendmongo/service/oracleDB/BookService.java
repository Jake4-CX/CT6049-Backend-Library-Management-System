package me.jack.lat.lmsbackendmongo.service.oracleDB;

import me.jack.lat.lmsbackendmongo.model.NewBook;
import me.jack.lat.lmsbackendmongo.util.DateUtil;
import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;

import java.sql.*;
import java.util.ArrayList;
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

        } catch (SQLException e) {
            logger.warning("SQL Error: " + e.getMessage());
            return new Error("Failed creating new book: " + e.getMessage());
        } catch (Exception e) {
            logger.warning("General Error: " + e.getMessage());
            return new Error("Failed creating new book: " + e.getMessage());
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

    public HashMap<String, Object>[] getBooks(String sort, String filter, Integer page, Integer limit) {
        ArrayList<HashMap<String, Object>> books = new ArrayList<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT b.*, "
                    + "a.authorFirstName, a.authorLastName, "
                    + "c.categoryName, c.categoryDescription, "
                    + " (SELECT COUNT(*) FROM loanedBooks l WHERE l.bookId = b.id AND l.returnedAt IS NULL) AS loanedQuantity "
                    + " FROM BOOKS b "
                    + " INNER JOIN bookAuthors a ON b.bookAuthorId = a.ID "
                    + " INNER JOIN bookCategories c ON b.bookCategoryId = c.ID "
                    + " ORDER BY b.ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            preparedStatement.setInt(1, (page - 1) * limit);
            preparedStatement.setInt(2, limit);

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
            logger.warning("Failed getting books: " + e.getMessage());
            return null;
        }

        return books.toArray(new HashMap[0]);

    }

    public HashMap<String, Object> getBookFromId(Integer bookId) {

        logger.info("Getting book from id: " + bookId);

        try (Connection connection = OracleDBUtil.getConnection()) {
            logger.info("Connection established");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT b.*, "
                    + "a.authorFirstName, a.authorLastName, "
                    + "c.categoryName, c.categoryDescription, "
                    + " (SELECT COUNT(*) FROM loanedBooks l WHERE l.bookId = b.id AND l.returnedAt IS NULL) AS loanedQuantity "
                    + " FROM BOOKS b "
                    + " INNER JOIN bookAuthors a ON b.bookAuthorId = a.ID "
                    + " INNER JOIN bookCategories c ON b.bookCategoryId = c.ID "
                    + " WHERE b.ID = ?");

            preparedStatement.setInt(1, bookId);

            logger.info("Prepared statement created");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                logger.info("Executed query");
                if (resultSet.next()) {
                    logger.info("Result set has next");
                    return new HashMap<>(){{
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
                }
            }
        } catch (Exception e) {
            logger.warning("Failed getting book from id: " + e.getMessage());
            return null;
        }

        return null;
    }

    public HashMap<String, Object> getBookFromId(Integer bookId, Integer userId) {

        try (Connection connection = OracleDBUtil.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT b.*, "
                    + " a.authorFirstName, a.authorLastName, "
                    + " c.categoryName, c.categoryDescription, "
                    + " lb.id AS loanedBookId, lb.loanedAt, lb.returnedAt, "
                    + " (SELECT COUNT(*) FROM loanedBooks l WHERE l.bookId = b.id AND l.returnedAt IS NULL) AS loanedQuantity "
                    + " FROM BOOKS b "
                    + " INNER JOIN bookAuthors a ON b.bookAuthorId = a.ID "
                    + " INNER JOIN bookCategories c ON b.bookCategoryId = c.ID "
                    + " LEFT JOIN loanedBooks lb on lb.userId = ? AND lb.bookId = b.id AND lb.returnedAt IS NULL "
                    + " WHERE b.ID = ?");
            // No need for loanFine, as this will only exist if returnedAt is not NULL

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, bookId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    HashMap<String, Object> book = new HashMap<>() {{
                        put("book", new HashMap<>() {{
                            put("bookId", resultSet.getInt("id"));
                            put("bookName", resultSet.getString("bookName"));
                            put("bookISBN", resultSet.getString("bookISBN"));
                            put("bookDescription", resultSet.getString("bookDescription"));
                            put("bookQuantity", resultSet.getInt("bookQuantity"));
                            put("bookThumbnailURL", resultSet.getString("bookThumbnailURL"));
                            put("bookPublishedDate", resultSet.getDate("bookPublishedDate"));
                            put("bookAuthorId", resultSet.getInt("bookAuthorId"));
                            put("bookCategory", new HashMap<>() {{
                                put("bookCategoryId", resultSet.getInt("bookCategoryId"));
                                put("categoryName", resultSet.getString("categoryName"));
                                put("categoryDescription", resultSet.getString("categoryDescription"));
                            }});
                            put("bookAuthor", new HashMap<>() {{
                                put("bookAuthorId", resultSet.getInt("bookAuthorId"));
                                put("authorFirstName", resultSet.getString("authorFirstName"));
                                put("authorLastName", resultSet.getString("authorLastName"));
                            }});
                        }});
                        put("booksLoaned", resultSet.getInt("loanedQuantity"));

                        if (resultSet.getObject("loanedBookId") != null) {
                            put("loanedBook", new HashMap<>() {{
                                put("loanedBookId", resultSet.getInt("loanedBookId"));
                                put("loanedAt", resultSet.getDate("loanedAt"));
                                put("returnedAt", resultSet.getDate("returnedAt"));
                            }});
                        }
                    }};

                    return book;
                }
            }
        } catch (Exception e) {
            logger.warning("Failed getting book from id: " + e.getMessage());
            return null;
        }

        return null;
    }

    public HashMap<String, Object>[] searchBooks(String searchQuery) {
        ArrayList<HashMap<String, Object>> books = new ArrayList<>();

            try (Connection connection = OracleDBUtil.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT b.*, "
                        + "a.authorFirstName, a.authorLastName, "
                        + "c.categoryName, c.categoryDescription, "
                        + " (SELECT COUNT(*) FROM loanedBooks l WHERE l.bookId = b.id AND l.returnedAt IS NULL) AS loanedQuantity "
                        + " FROM BOOKS b "
                        + " INNER JOIN bookAuthors a ON b.bookAuthorId = a.ID "
                        + " INNER JOIN bookCategories c ON b.bookCategoryId = c.ID "
                        + " WHERE LOWER(b.bookName) LIKE LOWER(?) "
                        + " ORDER BY b.ID");
                preparedStatement.setString(1, "%" + searchQuery + "%");

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
                logger.warning("Failed getting books: " + e.getMessage());
                return null;
            }

        return books.toArray(new HashMap[0]);
    }
}
