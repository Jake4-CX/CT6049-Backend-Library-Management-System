package me.jack.lat.lmsbackendmongo.resources.users;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.model.NewUser;
import me.jack.lat.lmsbackendmongo.service.UserService;
import me.jack.lat.lmsbackendmongo.util.OracleDBUtil;
import oracle.jdbc.OraclePreparedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Path("/users/register")
public class RegisterUserResource {

    @POST
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@HeaderParam("Database-Type") String databaseType, @Valid NewUser newUser) {

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return createUserSQL(newUser);
        } else {
            return createUserMongoDB(newUser);
        }

    }

    public Response createUserMongoDB(NewUser newUser) {
        Map<String, Object> response = new HashMap<>();

        UserService userService = new UserService();
        boolean isUserCreated = userService.createUser(newUser);

        if (isUserCreated) {
            response.put("message", "success");
            return Response.status(Response.Status.CREATED).entity(response).build();
        } else {
            response.put("message", "User with the same email already exists.");
            return Response.status(Response.Status.CONFLICT).entity(response).build();
        }
    }

    public Response createUserSQL(NewUser newUser) {
        Map<String, Object> response = new HashMap<>();

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT userEmail FROM users WHERE userEmail = ?");
            preparedStatement.setString(1, newUser.getUserEmail());

            if (preparedStatement.executeQuery().next()) {
                response.put("message", "User with the same email already exists.");
                return Response.status(Response.Status.CONFLICT).entity(response).type(MediaType.APPLICATION_JSON).build();
            }

        } catch (SQLException e) {
            System.err.println("[RegisterUserResource::SQL]: " + e.getMessage());
        }


        String userPassword = BCrypt.withDefaults().hashToString(12, newUser.getUserPassword().toCharArray());

        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (userEmail, userPassword) VALUES (?, ?)");
            preparedStatement.setString(1, newUser.getUserEmail());
            preparedStatement.setString(2, userPassword);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                response.put("message", "success");
                return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
            } else {
                response.put("message", "User with the same email already exists.");
                return Response.status(Response.Status.CONFLICT).entity(response).type(MediaType.APPLICATION_JSON).build();
            }

        } catch (SQLException e) {
            System.err.println("[RegisterUserResource::SQL]: " + e.getMessage());

        }

        response.put("message", "Internal Server Error");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
