package me.jack.lat.lmsbackendmongo.model;

import jakarta.validation.constraints.NotEmpty;

public class NewBookAuthor {

    @NotEmpty
    String authorFirstName;
    @NotEmpty
    String authorLastName;

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }
}
