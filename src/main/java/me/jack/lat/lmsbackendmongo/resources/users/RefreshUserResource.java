package me.jack.lat.lmsbackendmongo.resources.users;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.mongoDB.UserService;

import java.util.HashMap;
import java.util.Map;

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
    public Response refreshUser(@HeaderParam("Database-Type") String databaseType, @Valid RefreshToken refreshToken) {

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return refreshUserSQL(refreshToken);
        } else {
            return refreshUserMongoDB(refreshToken);
        }

    }

    public Response refreshUserMongoDB(RefreshToken refreshToken) {
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

    public Response refreshUserSQL(RefreshToken refreshToken) {

        me.jack.lat.lmsbackendmongo.service.oracleDB.UserService userService = new me.jack.lat.lmsbackendmongo.service.oracleDB.UserService();
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
