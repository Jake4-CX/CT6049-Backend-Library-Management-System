package me.jack.lat.lmsbackendmongo.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity("bookCategories")
public class BookCategory {

    @Id
    private ObjectId bookCategoryId;
    private String categoryName;
    private String categoryDescription;

    public ObjectId getBookCategoryId() {
        return bookCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }
}
