package me.jack.lat.lmsbackendmongo.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("bookCategories")
public class BookCategory {

    @Id
    private String bookCategoryId;
    private String categoryName;
    private String categoryDescription;
}
