package me.jack.lat.lmsbackendmongo.model;

import jakarta.validation.constraints.NotEmpty;

public class NewBookAuthor {

    @NotEmpty
    private String authorFirstName;
    @NotEmpty
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

    @Override
    public String toString() {
        return "NewBookAuthor{" +
                "authorFirstName='" + authorFirstName + '\'' +
                ", authorLastName='" + authorLastName + '\'' +
                '}';
    }
}
