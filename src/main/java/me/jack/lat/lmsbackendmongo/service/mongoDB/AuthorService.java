package me.jack.lat.lmsbackendmongo.service.mongoDB;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.filters.LogicalFilter;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.BookAuthor;
import me.jack.lat.lmsbackendmongo.model.NewBookAuthor;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.logging.Logger;

public class AuthorService {

    private final Datastore datastore;
    private static final Logger logger = Logger.getLogger(AuthorService.class.getName());

    public AuthorService() {
        this.datastore = MongoDBUtil.getMongoDatastore();
    }

    public BookAuthor getAuthorFromId(String authorId) {
        Query<BookAuthor> query = datastore.find(BookAuthor.class).filter("_id", new ObjectId(authorId));
        return query.first();
    }

    public List<Book> getBooksFromAuthor(BookAuthor author) {
        Query<Book> query = datastore.find(Book.class).filter("bookAuthor", author);
        return query.iterator().toList();
    }

    public boolean createAuthor(NewBookAuthor newAuthor) {

        if (isDuplicateAuthor(newAuthor)) {
            return false;
        }

        if (newAuthor.getAuthorFirstName() == null || newAuthor.getAuthorLastName() == null) {
            return false;
        }

        BookAuthor bookAuthor = new BookAuthor(newAuthor.getAuthorFirstName(), newAuthor.getAuthorLastName());

        try {
            datastore.save(bookAuthor);
        } catch (Exception e) {
            logger.warning("Failed saving author: " + e.getMessage());
            return false;
        }

        return true;
    }

    private boolean isDuplicateAuthor(NewBookAuthor newAuthor) {

        LogicalFilter filterRequirements = Filters.and(
                Filters.eq("authorFirstName", newAuthor.getAuthorFirstName()),
                Filters.eq("authorLastName", newAuthor.getAuthorLastName())
        );

        long count = datastore.find(BookAuthor.class)
                .filter(filterRequirements)
                .count();
        return count > 0;

    }

    public List<BookAuthor> getAuthors(String sort, String filter, int page, int limit) {
        FindOptions findOptions = new FindOptions().skip((page - 1) * limit).limit(limit);

        return datastore.find(BookAuthor.class).iterator(findOptions).toList();
    }
}
