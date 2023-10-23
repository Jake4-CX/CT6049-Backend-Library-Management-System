package me.jack.lat.lmsbackendmongo.entities;

import dev.morphia.annotations.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Entity("users")
public class User {

    private static final Logger logger = Logger.getLogger(User.class.getName());

    @Id
    private ObjectId userId;
    @Indexed(options = @IndexOptions(unique = true))
    private String userEmail;
    private String userPassword;
    private Role userRole;

    private Date userCreatedDate;
    private Date userUpdatedDate;

    private List<RefreshToken> refreshTokens;

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

    public void setRefreshTokens(List<RefreshToken> refreshTokens) {
        this.refreshTokens = refreshTokens;
    }

    public void addRefreshToken(RefreshToken refreshToken) {
        if (this.refreshTokens == null) {
            this.refreshTokens = new ArrayList<>();
        }
        this.refreshTokens.add(refreshToken);
    }

    public void removeRefreshToken(String refreshToken) {
        if (this.refreshTokens == null) {
            return;
        }
        this.refreshTokens.removeIf(token -> token.getRefreshToken().equals(refreshToken));
    }

    public List<RefreshToken> getRefreshTokens() {
        return refreshTokens;
    }

    @Entity("refreshTokens")
    public static class RefreshToken {

        @Indexed(options = @IndexOptions(unique = true))
        private String refreshToken;

        @Indexed(options = @IndexOptions(expireAfterSeconds = 0))
        private Date expirationDate;

        public RefreshToken() {
        }

        public RefreshToken(String refreshToken, Date expirationDate) {
            this.refreshToken = refreshToken;
            this.expirationDate = expirationDate;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public Date getExpirationDate() {
            return expirationDate;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public void setExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
        }
    }
}
