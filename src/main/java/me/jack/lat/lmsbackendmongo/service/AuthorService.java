package me.jack.lat.lmsbackendmongo.service;

import com.mongodb.client.MongoCollection;
import dev.morphia.query.filters.Filter;
import dev.morphia.query.filters.Filters;
import me.jack.lat.lmsbackendmongo.model.BookAuthor;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;
import org.bson.conversions.Bson;

public class AuthorService {

    private final MongoCollection<BookAuthor> authorsCollection;

    public AuthorService() {
        this.authorsCollection = MongoDBUtil.getMongoDatastore()
                .getCollection(BookAuthor.class);
    }

    public boolean createAuthor(BookAuthor newAuthor) {

        if (isDuplicateAuthor(newAuthor)) {
            return false;
        }

        if (newAuthor.getAuthorFirstName() == null || newAuthor.getAuthorLastName() == null) {
            return false;
        }

        authorsCollection.insertOne(newAuthor);

        return true;
    }

    private boolean isDuplicateAuthor(BookAuthor newAuthor) {

        Filter filterRequirements =  Filters.and(
                Filters.eq("authorFirstName", newAuthor.getAuthorFirstName()),
                Filters.eq("authorLastName", newAuthor.getAuthorLastName())
        );

        long count = authorsCollection.countDocuments(
                (Bson) filterRequirements
        );

        return count > 0;

    }
}
