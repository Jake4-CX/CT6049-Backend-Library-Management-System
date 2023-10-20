package me.jack.lat.lmsbackendmongo.resources.users;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Path("/users/login")
public class LoginUserResource {

    public static class LoginUser {

        @NotEmpty
        @Email
        private String userEmail;

        @NotEmpty
        private String userPassword;

        public LoginUser() {
        }

        public LoginUser(String userEmail, String userPassword) {
            this.userEmail = userEmail;
            this.userPassword = userPassword;
        }
    }

    @POST
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid LoginUser loginUser) {

        Map<String, Object> response = new HashMap<>();

        UserService userService = new UserService();
        boolean isUserCreated = userService.loginValidation(loginUser.userEmail, loginUser.userPassword);

        if (isUserCreated) {
            response.put("message", "success");
            return Response.status(Response.Status.OK).entity(response).build();
        } else {
            response.put("message", "Incorrect email or password.");
            return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
        }
    }
}
