package me.jack.lat.lmsbackendmongo.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import me.jack.lat.lmsbackendmongo.entities.BookAuthor;
import me.jack.lat.lmsbackendmongo.model.NewBookAuthor;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;
import org.bson.conversions.Bson;

public class AuthorService {

    private final MongoCollection<BookAuthor> authorsCollection;

    public AuthorService() {
        this.authorsCollection = MongoDBUtil.getMongoDatastore()
                .getDatabase()
                .getCollection("bookAuthors", BookAuthor.class);
    }

    public BookAuthor getAuthorFromId(String authorId) {
        return authorsCollection.find(
                (Bson) Filters.eq("_id", authorId)
        ).first();
    }

    public boolean createAuthor(NewBookAuthor newAuthor) {

//        if (isDuplicateAuthor(newAuthor)) {
//            return false;
//        }

//        if (newAuthor.getAuthorFirstName() == null || newAuthor.getAuthorLastName() == null) {
//            return false;
//        }

        BookAuthor bookAuthor = new BookAuthor(newAuthor.getAuthorFirstName(), newAuthor.getAuthorLastName());

        authorsCollection.insertOne(bookAuthor);

        return true;
    }

    private boolean isDuplicateAuthor(NewBookAuthor newAuthor) {

        Bson filterRequirements =  Filters.and(
                Filters.eq("authorFirstName", newAuthor.getAuthorFirstName()),
                Filters.eq("authorLastName", newAuthor.getAuthorLastName())
        );

        long count = authorsCollection.countDocuments(
                filterRequirements
        );

        return count > 0;

    }
}
