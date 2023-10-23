package me.jack.lat.lmsbackendmongo.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.mongodb.client.result.DeleteResult;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import io.jsonwebtoken.Claims;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.model.NewUser;
import me.jack.lat.lmsbackendmongo.util.JwtUtil;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class UserService {

    private final Datastore datastore;
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    public UserService() {
        this.datastore = MongoDBUtil.getMongoDatastore();
    }

    public boolean createUser(NewUser newUser) {

        if (userExists(newUser)) {
            return false;
        }

        if (newUser.getUserEmail() == null || newUser.getUserPassword() == null) {
            return false;
        }

        // Hash password with BCrypt
        String userPassword = BCrypt.withDefaults().hashToString(12, newUser.getUserPassword().toCharArray());

        User user = new User(newUser.getUserEmail(), userPassword);

        try {
            datastore.save(user);
        } catch (Exception e) {
            logger.warning("Failed saving user: " + e.getMessage());
            return false;
        }

        return true;

    }

    public boolean userExists(NewUser newUser) {
        long count = datastore.find(User.class)
                .filter(Filters.eq("userEmail", newUser.getUserEmail()))
                .count();
        return count > 0;
    }

    public HashMap<String, Object> loginValidation(String userEmail, String userPassword) {
        User user = datastore.find(User.class)
                .filter(Filters.eq("userEmail", userEmail))
                .first();
        if (user == null) {
            return null;
        }

        // Compare hashed password with provided password
        if (BCrypt.verifyer().verify(userPassword.toCharArray(), user.getUserPassword()).verified) {

            // Generate JWT token (access & refresh)
            String accessToken = JwtUtil.generateAccessToken(user.getUserId(), user.getUserRole().name());
            String refreshToken = JwtUtil.generateRefreshToken(user.getUserId());

            // Add refresh token to user
            user.addRefreshToken(new User.RefreshToken(refreshToken, JwtUtil.getExpirationDateFromRefreshToken(refreshToken)));
            datastore.save(user);

            // Clean up user object to be returned
            user.setUserPassword(null);
            user.setRefreshTokens(null);

            HashMap<String, Object> returnEntity = new HashMap<>();
            returnEntity.put("user", user);

            HashMap<String, String> token = new HashMap<>();

            token.put("accessToken", accessToken);
            token.put("refreshToken", refreshToken);

            returnEntity.put("token", token);

            return returnEntity;
        } else {
            return null;
        }
    }

    public User findUserById(String userId) {
        return datastore.find(User.class)
                .filter(Filters.eq("userId", userId))
                .first();
    }

    public User findUserByRefreshToken(String refreshToken) {
        return datastore.find(User.class)
                .filter(Filters.eq("refreshTokens.refreshToken", refreshToken))
                .first();
    }

    public HashMap<String, Object> refreshUser(String refreshToken) {

        try {
            logger.info("Attempting to refresh user with refresh token: " + refreshToken);
            Claims claims = JwtUtil.decodeRefreshToken(refreshToken);
            logger.info("Decoded refresh token: " + claims.toString());

            String userId = claims.getSubject();
            logger.info("User ID from refresh token: " + userId);

            User user = findUserByRefreshToken(refreshToken);
            logger.info("User from refresh token: " + user.getUserEmail());

            if (user == null) {
                // User not found
                return null;
            }

            if (!Objects.equals(user.getUserId(), userId)) {
                // User ID in token does not match user ID in database
                logger.warning("User ID in token does not match user ID in database");
                return null;
            }

            logger.info("User ID in token matches user ID in database");

            String newAccessToken = JwtUtil.generateAccessToken(user.getUserId(), user.getUserRole().name());
            String newRefreshToken = JwtUtil.generateRefreshToken(user.getUserId());

            logger.info("New access token: " + newAccessToken);
            logger.info("New refresh token: " + newRefreshToken);

            user.addRefreshToken(new User.RefreshToken(newRefreshToken, JwtUtil.getExpirationDateFromRefreshToken(newRefreshToken)));
            user.removeRefreshToken(refreshToken);

            datastore.save(user);

            logger.info("Saved user to database");

            HashMap<String, Object> returnEntity = new HashMap<>();
            returnEntity.put("accessToken", newAccessToken);
            returnEntity.put("refreshToken", newRefreshToken);

            logger.info("Returning new access token and refresh token");

            return returnEntity;

        } catch (Exception e) {
            // Invalid token
            logger.warning("Invalid refresh token: " + e.getMessage());
            return null;
        }
    }


}
