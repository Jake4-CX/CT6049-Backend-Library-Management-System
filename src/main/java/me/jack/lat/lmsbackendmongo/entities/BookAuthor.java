package me.jack.lat.lmsbackendmongo.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity("bookAuthors")
public class BookAuthor {

    @Id
    private ObjectId bookAuthorId;
    private String authorFirstName;
    private String authorLastName;

    public BookAuthor() {
    }

    public BookAuthor(String authorFirstName, String authorLastName) {
        this.authorFirstName = authorFirstName;
        this.authorLastName = authorLastName;
    }

    public String getBookAuthorId() {
        return bookAuthorId.toString();
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

    public void setBookAuthorId(ObjectId bookAuthorId) {
        this.bookAuthorId = bookAuthorId;
    }
}
