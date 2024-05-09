package me.jack.lat.lmsbackendmongo.resources.bookLoans;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;

import java.util.HashMap;
import java.util.Map;

@Path("/loans/overdue")
public class OverdueBookLoansResource {

    @GET
    @RestrictedRoles({User.Role.CHIEF_LIBRARIAN, User.Role.LIBRARIAN, User.Role.FINANCE_DIRECTOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverdueBookLoans() {

        return getOverdueBookLoansSQL();
    }

    public Response getOverdueBookLoansSQL() {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.LoanedBookService loanedBookService = new me.jack.lat.lmsbackendmongo.service.oracleDB.LoanedBookService();
        HashMap<String, Object>[] overdueBooks = loanedBookService.findAllOverdueBooks();

        response.put("overdueBooks", overdueBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
