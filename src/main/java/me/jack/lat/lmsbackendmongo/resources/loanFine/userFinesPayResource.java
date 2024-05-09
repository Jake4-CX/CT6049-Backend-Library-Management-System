package me.jack.lat.lmsbackendmongo.resources.loanFine;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;

import me.jack.lat.lmsbackendmongo.service.oracleDB.LoanFinesService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/users/me/fines/{loanedBookId}/pay")
public class userFinesPayResource {

    private static final Logger logger = Logger.getLogger(userFinesPayResource.class.getName());

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.CHIEF_LIBRARIAN, User.Role.LIBRARIAN, User.Role.FINANCE_DIRECTOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Response userFinesPay(@Context ContainerRequestContext requestContext, @PathParam("loanedBookId") String loanedBookId) {

        String userId = (String) requestContext.getProperty("userId");

        return userFinesPaySQL(loanedBookId, userId);
    }

    private Response userFinesPaySQL(String loanedBookId, String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer.valueOf(loanedBookId);
        } catch (NumberFormatException e) {
            response.put("message", "No loanFine found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        LoanFinesService loanFinesService = new LoanFinesService();
        Error error = loanFinesService.payFine(Integer.valueOf(loanedBookId), Integer.valueOf(userId));

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
