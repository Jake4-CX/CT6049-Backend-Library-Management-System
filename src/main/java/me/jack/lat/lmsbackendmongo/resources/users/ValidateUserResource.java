package me.jack.lat.lmsbackendmongo.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import java.util.HashMap;
import java.util.Map;

@Path("/users/validate")
public class ValidateUserResource {

    @POST
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateUser(@HeaderParam("Authorization") String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            HashMap<String, String> error = new HashMap<>();
            error.put("message", "Missing or invalid token");
            error.put("type", "401");
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).type(MediaType.APPLICATION_JSON).build();
        }

        String authorizationToken = authorizationHeader.substring("Bearer".length()).trim();

        return validateUserSQL(authorizationToken);

    }

    public Response validateUserSQL(String authorizationToken) {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.UserService userService = new me.jack.lat.lmsbackendmongo.service.oracleDB.UserService();
        HashMap<String, Object> returnEntity = userService.validateAccessToken(authorizationToken);

        if (returnEntity != null) {
            response.put("message", "User validated.");
            response.put("data", returnEntity);

            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();

        } else {
            response.put("message", "Invalid access token.");
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }
}
