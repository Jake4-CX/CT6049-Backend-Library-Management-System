package me.jack.lat.lmsbackendmongo.resources.users;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.model.NewUser;
import me.jack.lat.lmsbackendmongo.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Path("/users/login")
public class LoginUserResource {

    @POST
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid NewUser newUser) {

        Map<String, Object> response = new HashMap<>();

        UserService userService = new UserService();
        HashMap<String, Object> returnEntity = userService.loginValidation(newUser.getUserEmail(), newUser.getUserPassword());

        if (returnEntity != null) {
            response.put("message", "success");
            response.put("data", returnEntity);
            return Response.status(Response.Status.OK).entity(response).build();
        } else {
            response.put("message", "Incorrect email or password.");
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        }
    }
}
