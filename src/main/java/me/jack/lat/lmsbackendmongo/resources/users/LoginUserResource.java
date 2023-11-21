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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Path("/users/login")
public class LoginUserResource {

    @POST
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(@HeaderParam("Database-Type") String databaseType, @Valid NewUser newUser) {

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return loginUserSQL(newUser);
        } else {
            return loginUserMongoDB(newUser);
        }

    }

    public Response loginUserMongoDB(NewUser newUser) {
        Map<String, Object> response = new HashMap<>();

        UserService userService = new UserService();
        HashMap<String, Object> returnEntity = userService.loginValidation(newUser.getUserEmail(), newUser.getUserPassword());

        if (returnEntity != null) {
            response.put("message", "success");
            response.put("data", returnEntity);
            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", "Incorrect email or password.");
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }

    public Response loginUserSQL(NewUser newUser) {
        Map<String, Object> response = new HashMap<>();


        try (Connection connection = OracleDBUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM USERS WHERE userEmail = ?");
            preparedStatement.setString(1, newUser.getUserEmail());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String userPassword = resultSet.getString("userPassword");

                if (BCrypt.verifyer().verify(newUser.getUserPassword().toCharArray(), userPassword).verified) {
                    response.put("message", "success");
                    response.put("data", "This would contain user data.");

                    return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
                } else {
                    response.put("message", "Incorrect email or password.");
                    return Response.status(Response.Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON).build();
                }
            }

        } catch (SQLException e) {
            System.err.println("[LoginUserResource::SQL]: " + e.getMessage());

        }

        response.put("message", "Internal Server Error");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON).build();

    }
}
