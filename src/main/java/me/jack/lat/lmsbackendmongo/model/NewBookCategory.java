package me.jack.lat.lmsbackendmongo.model;

import jakarta.validation.constraints.NotEmpty;

public class NewBookCategory {

    @NotEmpty(message = "Category name cannot be empty")
    private String categoryName;
    @NotEmpty(message = "Category description cannot be empty")
    private String categoryDescription;

    public NewBookCategory() {
    }

    public NewBookCategory(String categoryName, String categoryDescription) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
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
