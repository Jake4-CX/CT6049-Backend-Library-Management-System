package me.jack.lat.lmsbackendmongo.resources.bookLoans;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.LoanedBook;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.service.LoanedBookService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/loans/overdue")
public class OverdueBookLoansResource {

    @GET
    @RestrictedRoles({User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverdueBookLoans() {

        LoanedBookService loanedBookService = new LoanedBookService();
        List<LoanedBook> overdueBooks = loanedBookService.findAllOverdueBooks();

        overdueBooks.forEach(loanedBook -> {
            User user = loanedBook.getUser();

            user.setUserPassword(null);
            user.setRefreshTokens(null);
            loanedBook.setUser(user);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("overdueBooks", overdueBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}