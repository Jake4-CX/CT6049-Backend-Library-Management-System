package me.jack.lat.lmsbackendmongo.resources.bookLoans;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;

import java.util.HashMap;
import java.util.Map;

@Path("/users/me/loans")
public class GetAllCurrentUserBookLoansResource {

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.CHIEF_LIBRARIAN, User.Role.LIBRARIAN, User.Role.FINANCE_DIRECTOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCurrentUserBookLoans(@Context ContainerRequestContext requestContext) {

        String userId = (String) requestContext.getProperty("userId");

        return getAllCurrentUserBookLoansSQL(userId);
    }

    public Response getAllCurrentUserBookLoansSQL(String userId) {
        Map<String, Object> response = new HashMap<>();
        me.jack.lat.lmsbackendmongo.service.oracleDB.LoanedBookService loanedBookService = new me.jack.lat.lmsbackendmongo.service.oracleDB.LoanedBookService();
        HashMap<String, Object>[] loanedBooks = loanedBookService.findCurrentLoansForUser(Integer.valueOf(userId));

        response.put("loanedBooks", loanedBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
