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

@Path("/users/register")
public class RegisterUserResource {

    @POST
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid NewUser newUser) {

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
}
