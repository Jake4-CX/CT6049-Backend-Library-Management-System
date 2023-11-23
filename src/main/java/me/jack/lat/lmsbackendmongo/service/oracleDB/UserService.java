package me.jack.lat.lmsbackendmongo.service.oracleDB;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import me.jack.lat.lmsbackendmongo.model.NewUser;
import me.jack.lat.lmsbackendmongo.util.JwtUtil;
import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.logging.Logger;

public class UserService {

    private static final Dotenv dotenv = Dotenv.configure().load();
    private static final int REFRESH_TOKEN_EXPIRATION_TIME = Integer.parseInt(dotenv.get("REFRESH_TOKEN_EXPIRATION_TIME"));
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    public UserService() {
    }

    public Error createUser(NewUser newUser) {

        if (userExists(newUser)) {
            return new Error("User with the same email already exists.");
        }

        // No pre-existing user with the same email, create new user

        String userPassword = BCrypt.withDefaults().hashToString(12, newUser.getUserPassword().toCharArray());

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (userEmail, userPassword) VALUES (?, ?)");
            preparedStatement.setString(1, newUser.getUserEmail());
            preparedStatement.setString(2, userPassword);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                return null;
            } else {
                return new Error("Failed creating new user");
            }

        } catch (SQLException e) {
            return new Error("Failed creating new user");
        }

    }

    public boolean userExists(NewUser newUser) {
        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT userEmail FROM users WHERE userEmail = ?");
            preparedStatement.setString(1, newUser.getUserEmail());

            return preparedStatement.executeQuery().next();

        } catch (SQLException e) {
            logger.warning("Failed saving user: " + e.getMessage());
            return false;
        }
    }

    public HashMap<String, Object> loginValidation(String userEmail, String userPassword) {

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE userEmail = ?");
            preparedStatement.setString(1, userEmail);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                logger.info("User found, email: " + resultSet.getString("userEmail") + " password: " + resultSet.getString("userPassword"));
                String databaseUserPassword = resultSet.getString("userPassword");

                // Compare hashed password with provided password
                if (BCrypt.verifyer().verify(userPassword.toCharArray(), databaseUserPassword).verified) {

                    Integer userId = resultSet.getInt("id");
                    String userRole = resultSet.getString("userRole");

                    // Generate JWT token (access & refresh)
                    String accessToken = JwtUtil.generateAccessToken(String.valueOf(userId), userRole);
                    String refreshToken = JwtUtil.generateRefreshToken(String.valueOf(userId));

                    assignUserRefreshToken(userId, refreshToken);

                    HashMap<String, Object> userEntity = new HashMap<>(){{
                        put("userId", userId);
                        put("userEmail", resultSet.getString("userEmail"));
                        put("userRole", userRole);
                        put("userCreatedDate", resultSet.getDate("userCreatedDate"));
                        put("userUpdatedDate", resultSet.getDate("userUpdatedDate"));
                    }};

                    HashMap<String, String> userToken = new HashMap<>(){{
                        put("accessToken", accessToken);
                        put("refreshToken", refreshToken);
                    }};

                    return new HashMap<>(){{
                        put("user", userEntity);
                        put("token", userToken);
                    }};
                }
            }
        } catch (SQLException e) {
            logger.warning("Failed Login Validation: " + e.getMessage());
            return null;

        }

        return null;
    }

    public void assignUserRefreshToken(Integer userId, String refreshToken) {
        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO REFRESHTOKENS (refreshToken, userId, expirationDate) VALUES (?, ?, ?)");
            preparedStatement.setString(1, refreshToken);
            preparedStatement.setInt(2, userId);
            preparedStatement.setDate(3, new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME));

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logger.warning("Failed Assigning User's Refresh Token: " + e.getMessage());
        }
    }

    public void unassignUserRefreshToken(Integer userId, String refreshToken) {
        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM REFRESHTOKENS WHERE refreshToken = ? AND userId = ?");
            preparedStatement.setString(1, refreshToken);
            preparedStatement.setInt(2, userId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logger.warning("Failed Deleting User's Refresh Token: " + e.getMessage());
        }
    }

    public HashMap<String, Object> findUserById(Integer userId) {
        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE id = ?");
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                return new HashMap<>(){{
                    put("userId", userId);
                    put("userEmail", resultSet.getString("userEmail"));
                    put("userRole", resultSet.getString("userRole"));
                    put("userCreatedDate", resultSet.getDate("userCreatedDate"));
                    put("userUpdatedDate", resultSet.getDate("userUpdatedDate"));
                }};
            }
        } catch (SQLException e) {
            return null;

        }

        return null;
    }

    public HashMap<String, Object> refreshUser(String refreshToken) {

        try {
            Claims claims = JwtUtil.decodeRefreshToken(refreshToken);
            String userId = claims.getSubject();

            HashMap<String, Object> user = findUserById(Integer.valueOf(userId));

            if (user == null) {
                // User not found
                return null;
            }

            if ((user.get("userId") != Integer.valueOf(userId))) {
                // User ID in token does not match user ID in database
                return null;
            }

            // Generate JWT token (access & refresh)
            String newAccessToken = JwtUtil.generateAccessToken(userId, user.get("userRole").toString());
            String newRefreshToken = JwtUtil.generateRefreshToken(userId);

            assignUserRefreshToken(Integer.valueOf(userId), newRefreshToken);
            unassignUserRefreshToken(Integer.valueOf(userId), refreshToken);

            return new HashMap<>(){{
                put("accessToken", newAccessToken);
                put("refreshToken", newRefreshToken);
            }};

        } catch (Exception e) {
            // Invalid token
            return null;
        }
    }

    public HashMap<String, Object> validateAccessToken(String accessToken) {

        try {
            Claims claims = JwtUtil.decodeAccessToken(accessToken);
            String userId = claims.getSubject();

            return findUserById(Integer.valueOf(userId));

        } catch (Exception e) {
            return null;
        }
    }
}
