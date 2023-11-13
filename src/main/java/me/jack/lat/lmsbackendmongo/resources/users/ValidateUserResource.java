package me.jack.lat.lmsbackendmongo.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Path("/users/validate")
public class ValidateUserResource {

    @POST
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateUser(@HeaderParam("Database-Type") String databaseType, @HeaderParam("Authorization") String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            HashMap<String, String> error = new HashMap<>();
            error.put("message", "Missing or invalid token");
            error.put("type", "401");
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).type(MediaType.APPLICATION_JSON).build();
        }

        String authorizationToken = authorizationHeader.substring("Bearer".length()).trim();

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return validateUserSQL(authorizationToken);
        } else {
            return validateUserMongoDB(authorizationToken);
        }

    }

    public Response validateUserMongoDB(String authorizationToken) {

        UserService userService = new UserService();
        HashMap<String, Object> response = new HashMap<>();

        User userEntity = userService.validateAccessToken(authorizationToken);

        if (userEntity != null) {
            response.put("message", "User validated.");
            response.put("data", userEntity);

            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();

        } else {
            response.put("message", "Invalid access token.");
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON).build();

        }

    }

    public Response validateUserSQL(String authorizationToken) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Not implemented yet - SQL");

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
