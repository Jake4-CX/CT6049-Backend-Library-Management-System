package me.jack.lat.lmsbackendmongo.resources.users;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.model.NewUser;

import java.util.HashMap;
import java.util.Map;

@Path("/users/login")
public class LoginUserResource {

    @POST
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(@Valid NewUser newUser) {
        return loginUserSQL(newUser);

    }

    public Response loginUserSQL(NewUser newUser) {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.UserService userService = new me.jack.lat.lmsbackendmongo.service.oracleDB.UserService();
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
}
