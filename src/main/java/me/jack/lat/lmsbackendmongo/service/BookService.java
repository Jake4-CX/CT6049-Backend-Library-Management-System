package me.jack.lat.lmsbackendmongo.service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.BookAuthor;
import me.jack.lat.lmsbackendmongo.model.NewBook;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookService {

    private final MongoCollection<Book> booksCollection;

    public BookService() {

        this.booksCollection = MongoDBUtil.getMongoDatastore()
                .getDatabase()
                .getCollection("books", Book.class);
    }

    public List<Book> getBooks(String sort, String filter, int page, int limit) {
        List<Book> books = new ArrayList<>();
        Bson filterQuery = null;

        if (filter != null) {
            filterQuery = Filters.or(
                    Filters.regex("bookName", filter, "i"),
                    Filters.regex("bookAuthor.name", filter, "i")
            );
        }

        // Sort based on sort criteria (e.g., recent, least recent, etc.)
        Bson sortQuery = null;

        switch (sort.toLowerCase()) {
            case "recent":
                sortQuery = Sorts.descending("bookPublishedDate"); // Sort by bookPublishedDate descending (recent)
                break;
            case "least-recent":
                sortQuery = Sorts.ascending("bookPublishedDate"); // Sort by bookPublishedDate ascending (least recent)
                break;
            case "name":
                sortQuery = Sorts.ascending("bookName"); // Sort by bookName ascending
                break;
            default:
                sortQuery = Sorts.descending("bookPublishedDate");
                break;
        }

        int skip = (page - 1) * limit;

        FindIterable<Book> bookCursor;
        if (filterQuery != null) {
            bookCursor = booksCollection.find(filterQuery)
                    .sort(sortQuery)
                    .skip(skip)
                    .limit(limit);
        } else {
            bookCursor = booksCollection.find()
                    .sort(sortQuery)
                    .skip(skip)
                    .limit(limit);
        }

        for (Book book : bookCursor) {
            books.add(book);
        }

        return books;
    }

    public boolean createBook(NewBook newBook) {

        if (isDuplicateBookName(newBook.getBookName())) {
            return false;
        }

        // ToDo: Get bookAuthor and bookCategory from database

        AuthorService authorService = new AuthorService();

        BookAuthor bookAuthor = authorService.getAuthorFromId(newBook.getBookAuthorId().toString());

        if (bookAuthor == null) {
            // ToDo: Return error message - author not found
            return false;
        }

//        if (newBook.getBookCategory() == null) {
//            newBook.setBookCategory(new BookCategory());
//        }

        booksCollection.insertOne(
                new Book(
                        newBook.getBookName(),
                        newBook.getBookISBN(),
                        newBook.getBookDescription(),
                        newBook.getBookQuantity(),
                        new Date(),
                        null,
                        bookAuthor
                )
        );

        return true;
    }

    private boolean isDuplicateBookName(String bookName) {
        long count = booksCollection.countDocuments(Filters.eq("bookName", bookName));
        return count > 0;
    }
}
