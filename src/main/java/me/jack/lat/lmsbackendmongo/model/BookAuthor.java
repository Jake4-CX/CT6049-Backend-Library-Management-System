package me.jack.lat.lmsbackendmongo.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity("bookAuthors")
public class BookAuthor {

    @Id
    private String bookAuthorId;

    private String authorFirstName;
    private String authorLastName;

    public String getBookAuthorId() {
        return bookAuthorId;
    }

    public void setBookAuthorId(String bookAuthorId) {
        this.bookAuthorId = bookAuthorId;
    }

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }
}
