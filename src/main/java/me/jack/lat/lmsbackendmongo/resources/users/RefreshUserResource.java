package me.jack.lat.lmsbackendmongo.resources.users;

import jakarta.validation.Valid;
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

@Path("/users/refresh")
public class RefreshUserResource {

    public static class RefreshToken {
        @NotEmpty(message = "Missing refreshToken")
        private String refreshToken;

        public RefreshToken() {
        }

        public RefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    @POST
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshUser(@Valid RefreshToken refreshToken) {

        UserService userService = new UserService();
        HashMap<String, Object> returnEntity = userService.refreshUser(refreshToken.getRefreshToken());

        if (returnEntity != null) {
            return Response.status(Response.Status.OK).entity(returnEntity).type(MediaType.APPLICATION_JSON).build();
        } else {
            HashMap<String, String> error = new HashMap<>();
            error.put("message", "Invalid refresh token.");
            error.put("refreshToken", refreshToken.getRefreshToken());
            error.put("type", "401");
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).type(MediaType.APPLICATION_JSON).build();
        }

    }
}
