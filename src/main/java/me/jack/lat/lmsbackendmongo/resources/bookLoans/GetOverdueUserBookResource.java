package me.jack.lat.lmsbackendmongo.resources.bookLoans;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.LoanedBook;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.service.LoanedBookService;
import me.jack.lat.lmsbackendmongo.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/users/me/loans/overdue")
public class GetOverdueUserBookResource {

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverdueUserBookLoans(@Context ContainerRequestContext requestContext) {

        Map<String, Object> response = new HashMap<>();

        String userId = (String) requestContext.getProperty("userId");

        UserService userService = new UserService();
        User user = userService.findUserById(userId);

        LoanedBookService loanedBookService = new LoanedBookService();
        List<LoanedBook> loanedBooks = loanedBookService.findOverdueBooksForUser(user);

        loanedBooks.forEach(loanedBook -> {
            User loanedBookUser = loanedBook.getUser();

            loanedBookUser.setUserPassword(null);
            loanedBookUser.setRefreshTokens(null);
            loanedBook.setUser(loanedBookUser);
        });

        response.put("overdueBooks", loanedBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
