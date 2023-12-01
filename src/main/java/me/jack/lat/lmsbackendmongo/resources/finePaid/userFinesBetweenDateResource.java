package me.jack.lat.lmsbackendmongo.resources.finePaid;

import jakarta.ws.rs.*;
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
import me.jack.lat.lmsbackendmongo.service.oracleDB.LoanFinesService;
import me.jack.lat.lmsbackendmongo.util.DateUtil;

import java.sql.Date;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/users/me/fines/between")
public class userFinesBetweenDateResource {

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userFinesBetweenDate(@HeaderParam("Database-Type") String databaseType, @Context ContainerRequestContext requestContext, @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {

        if (startDate == null || startDate.isEmpty()) {
            // default startDate as today minus 30 days (as Date)
            startDate = DateUtil.convertDateToStringFromWeb(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000));
        }

        if (endDate == null || endDate.isEmpty()) {
            endDate = DateUtil.convertDateToStringFromWeb(new Date(System.currentTimeMillis()));
        }

        String userId = (String) requestContext.getProperty("userId");

        HashMap<String, Object> response = new HashMap<>();

        try {

            Date startDateDate = new Date(DateUtil.convertStringToDate((startDate)).getTime());
            Date endDateDate = new Date(DateUtil.convertStringToDate((endDate)).getTime());

            response.put("startDate", startDateDate);
            response.put("endDate", endDateDate);

            if (startDateDate.after(endDateDate)) {
                response.put("message", "Start date cannot be after end date");
                return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
            }

            if (databaseType == null || databaseType.isEmpty()) {
                databaseType = DatabaseTypeEnum.MONGODB.toString();
            }

            if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
                return userFinesBetweenDateSQL(userId, startDateDate, endDateDate);
            } else {
                return userFinesBetweenDateMongoDB(userId, startDateDate, endDateDate);
            }

        } catch (ParseException e) {
            response.put("message", "Failed to parse date: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        }
    }

    public Response userFinesBetweenDateMongoDB(String userId, Date startDate, Date endDate) {
        Map<String, Object> response = new HashMap<>();

        UserService userService = new UserService();
        User user = userService.findUserById(userId);

        LoanedBookService loanedBookService = new LoanedBookService();
        List<LoanedBook> loanedBooks = loanedBookService.findFinesForUserBetweenDate(user, startDate, endDate);

        loanedBooks.forEach(loanedBook -> {
            User loanedBookUser = loanedBook.getUser();

            loanedBookUser.setUserPassword(null);
            loanedBookUser.setRefreshTokens(null);
            loanedBook.setUser(loanedBookUser);
        });

        response.put("loanedBooks", loanedBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

    public Response userFinesBetweenDateSQL(String userId, Date startDate, Date endDate) {
        HashMap<String, Object> response = new HashMap<>();

        LoanFinesService loanFinesService = new LoanFinesService();
        HashMap<String, Object>[] loanFines = loanFinesService.findFinesForUserBetweenDate(Integer.parseInt(userId), startDate, endDate);

        response.put("loanedBooks", loanFines);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();

    }
}
