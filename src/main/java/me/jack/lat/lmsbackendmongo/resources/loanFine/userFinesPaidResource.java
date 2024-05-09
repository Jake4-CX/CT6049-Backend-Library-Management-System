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

@Path("/users/me/fines/paid")
public class userFinesPaidResource {

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.CHIEF_LIBRARIAN, User.Role.LIBRARIAN, User.Role.FINANCE_DIRECTOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userFinesPaid(@Context ContainerRequestContext requestContext) {

        String userId = (String) requestContext.getProperty("userId");

        return userFinesPaidSQL(userId);
    }

    public Response userFinesPaidSQL(String userId) {
        Map<String, Object> response = new HashMap<>();

        LoanFinesService loanFinesService = new LoanFinesService();
        HashMap<String, Object>[] loanFines = loanFinesService.findFinesPaidForUser(Integer.parseInt(userId));

        response.put("loanedBooks", loanFines);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
