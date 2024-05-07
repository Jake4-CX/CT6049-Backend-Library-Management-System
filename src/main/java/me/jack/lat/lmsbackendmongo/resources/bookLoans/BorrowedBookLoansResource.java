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

@Path("/loans/borrowed")
public class BorrowedBookLoansResource {

    @GET
    @RestrictedRoles({User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBorrowedBookLoans() {
        return getBorrowedBookLoansSQL();
    }

    public Response getBorrowedBookLoansSQL() {
        Map<String, Object> response = new HashMap<>();
        me.jack.lat.lmsbackendmongo.service.oracleDB.LoanedBookService loanedBookService = new me.jack.lat.lmsbackendmongo.service.oracleDB.LoanedBookService();
        HashMap<String, Object>[] borrowedBooks = loanedBookService.findAllBorrowedBooks();

        response.put("borrowedBooks", borrowedBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
