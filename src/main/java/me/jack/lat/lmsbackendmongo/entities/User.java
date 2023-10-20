package me.jack.lat.lmsbackendmongo.entities;

import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity("users")
public class User {

    @Id
    private ObjectId userId;
    @Indexed(options = @IndexOptions(unique = true))
    private String userEmail;
    private String userPassword;
    private Role userRole;

    private Date userCreatedDate;
    private Date userUpdatedDate;

    public enum Role {
        ADMIN, USER
    }

    public User() {
    }

    public User(String userEmail, String userPassword) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userRole = Role.USER;
    }

    @PrePersist
    public void prePersist() {
        Date now = new Date();
        this.userUpdatedDate = now;
        if (this.userCreatedDate == null) {  // Only set createdDate if it's null
            this.userCreatedDate = now;
        }
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Role getUserRole() {
        return userRole;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }

    public Date getUserCreatedDate() {
        return userCreatedDate;
    }

    public void setUserCreatedDate(Date userCreatedDate) {
        this.userCreatedDate = userCreatedDate;
    }

    public Date getUserUpdatedDate() {
        return userUpdatedDate;
    }

    public void setUserUpdatedDate(Date userUpdatedDate) {
        this.userUpdatedDate = userUpdatedDate;
    }

    public String getUserId() {
        return userId.toString();
    }
}
