package me.jack.lat.lmsbackendmongo.model;

import jakarta.validation.constraints.NotEmpty;

public class NewBookAuthor {

    @NotEmpty(message = "Author first name cannot be empty")
    private String authorFirstName;
    @NotEmpty(message = "Author last name cannot be empty")
    private String authorLastName;

    public NewBookAuthor() {
    }

    public NewBookAuthor(String authorFirstName, String authorLastName) {
        this.authorFirstName = authorFirstName;
        this.authorLastName = authorLastName;
    }

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }

}
