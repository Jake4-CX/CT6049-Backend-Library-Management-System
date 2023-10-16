package me.jack.lat.lmsbackendmongo.service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.InsertOneResult;
import me.jack.lat.lmsbackendmongo.model.Book;
import me.jack.lat.lmsbackendmongo.model.BookAuthor;
import me.jack.lat.lmsbackendmongo.model.BookCategory;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class BookService {

    private final MongoCollection<Book> booksCollection;

    public BookService() {

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true)
                .register(BookAuthor.class)
                .register(BookCategory.class)
                .build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));

        this.booksCollection = MongoDBUtil.getMongoClient()
                .getDatabase("lms")
                .withCodecRegistry(pojoCodecRegistry)
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

    public boolean createBook(Book newBook) {

        if (isDuplicateBookName(newBook.getBookName())) {
            return false;
        }

        if (newBook.getBookAuthor() == null) {
            newBook.setBookAuthor(new BookAuthor());
        }
        if (newBook.getBookCategory() == null) {
            newBook.setBookCategory(new BookCategory());
        }

        booksCollection.insertOne(newBook);

        return true;
    }

    private boolean isDuplicateBookName(String bookName) {
        long count = booksCollection.countDocuments(Filters.eq("bookName", bookName));
        return count > 0;
    }
}
