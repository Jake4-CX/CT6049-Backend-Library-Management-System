package me.jack.lat.lmsbackendmongo.resources.finePaid;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.oracleDB.LoanFinesService;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

@Path("/users/me/fines/{fineId}/pay")
public class userFinesPayResource {

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response userFinesPay(@HeaderParam("Database-Type") String databaseType, @Context ContainerRequestContext requestContext, @PathParam("fineId") String fineId) {

        String userId = (String) requestContext.getProperty("userId");

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return userFinesPaySQL(fineId, userId);
        } else {
            return userFinesPayMongoDB(fineId, userId);
        }
    }

    private Response userFinesPayMongoDB(String userId, String fineId) {
        Map<String, Object> response = new HashMap<>();

        try {
            new ObjectId(fineId);

        } catch (Exception e) {
            response.put("message", "No loanFine found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        // ToDo: Implement MongoDB version of this method

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

    private Response userFinesPaySQL(String userId, String fineId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer.valueOf(fineId);
        } catch (NumberFormatException e) {
            response.put("message", "No loanFine found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        LoanFinesService loanFinesService = new LoanFinesService();
        Error error = loanFinesService.payFine(Integer.valueOf(userId), Integer.valueOf(fineId));

        if (error != null) {
            response.put("error", new HashMap<>(){{
                put("message", error.getMessage());
                put("type", 400);
            }});
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", "Fine paid successfully");
            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }
}
