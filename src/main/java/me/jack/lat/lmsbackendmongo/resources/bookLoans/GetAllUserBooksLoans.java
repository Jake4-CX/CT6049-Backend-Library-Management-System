package me.jack.lat.lmsbackendmongo.resources.bookLoans;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.LoanedBook;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.mongoDB.LoanedBookService;
import me.jack.lat.lmsbackendmongo.service.mongoDB.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/users/me/loans/all")
public class GetAllUserBooksLoans {

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUserBookLoans(@HeaderParam("Database-Type") String databaseType, @Context ContainerRequestContext requestContext) {

        String userId = (String) requestContext.getProperty("userId");

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return getAllUserBookLoansSQL(userId);
        } else {
            return getAllUserBookLoansMongoDB(userId);
        }
    }

    public Response getAllUserBookLoansMongoDB(String userId) {
        Map<String, Object> response = new HashMap<>();

        UserService userService = new UserService();
        User user = userService.findUserById(userId);

        LoanedBookService loanedBookService = new LoanedBookService();
        List<LoanedBook> loanedBooks = loanedBookService.findLoanedBooksForUser(user);

        loanedBooks.forEach(loanedBook -> {
            User loanedBookUser = loanedBook.getUser();

            loanedBookUser.setUserPassword(null);
            loanedBookUser.setRefreshTokens(null);
            loanedBook.setUser(loanedBookUser);
        });

        response.put("loanedBooks", loanedBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

    public Response getAllUserBookLoansSQL(String userId) {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.LoanedBookService loanedBookService = new me.jack.lat.lmsbackendmongo.service.oracleDB.LoanedBookService();
        HashMap<String, Object>[] loanedBooks = loanedBookService.getLoanedBooksForUser(Integer.valueOf(userId));

        response.put("loanedBooks", loanedBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
