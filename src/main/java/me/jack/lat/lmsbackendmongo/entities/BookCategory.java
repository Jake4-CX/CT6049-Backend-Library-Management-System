package me.jack.lat.lmsbackendmongo.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

@Entity("bookCategories")
public class BookCategory {

    @Id
    private ObjectId bookCategoryId;
    @Indexed(options = @IndexOptions(unique = true))
    private String categoryName;
    private String categoryDescription;

    public BookCategory() {
    }

    public BookCategory(String categoryName, String categoryDescription) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }

    public String getBookCategoryId() {
        return bookCategoryId.toString();
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
