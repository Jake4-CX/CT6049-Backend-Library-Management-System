package me.jack.lat.lmsbackendmongo.service.mongoDB;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.BookCategory;
import me.jack.lat.lmsbackendmongo.model.NewBookCategory;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.logging.Logger;

public class CategoryService {

    private final Datastore datastore;
    private static final Logger logger = Logger.getLogger(CategoryService.class.getName());

    public CategoryService() {
        this.datastore = MongoDBUtil.getMongoDatastore();
    }

    public BookCategory getCategoryFromId(String categoryId) {
        Query<BookCategory> query = datastore.find(BookCategory.class).filter("_id", new ObjectId(categoryId));
        return query.first();
    }

    public List<Book> getBooksFromCategory(BookCategory category) {
        Query<Book> query = datastore.find(Book.class).filter("bookCategory", category);
        return query.iterator().toList();
    }

    public boolean createCategory(NewBookCategory newBookCategory) {

        if (isDuplicateCategory(newBookCategory)) {
            return false;
        }

        if (newBookCategory.getCategoryName() == null || newBookCategory.getCategoryDescription() == null) {
            return false;
        }

        BookCategory bookCategory = new BookCategory(newBookCategory.getCategoryName(), newBookCategory.getCategoryDescription());

        try {
            datastore.save(bookCategory);
        } catch (Exception e) {
            logger.warning("Failed saving category: " + e.getMessage());
            return false;
        }

        return true;

    }

    public boolean isDuplicateCategory(NewBookCategory newBookCategory) {

        long count = datastore.find(BookCategory.class)
                .filter(Filters.eq("categoryName", newBookCategory.getCategoryName()))
                .count();
        return count > 0;
    }

    public List<BookCategory> getCategories(String sort, String filter, int page, int limit) {
        FindOptions findOptions = new FindOptions().skip((page - 1) * limit).limit(limit);

        return datastore.find(BookCategory.class).iterator(findOptions).toList();
    }
}
