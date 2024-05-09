package me.jack.lat.lmsbackendmongo.resources.loanFine;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.service.oracleDB.LoanFinesService;
import me.jack.lat.lmsbackendmongo.util.DateUtil;

import java.sql.Date;
import java.text.ParseException;
import java.util.HashMap;

@Path("/users/me/fines/between")
public class userFinesBetweenDateResource {

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.CHIEF_LIBRARIAN, User.Role.LIBRARIAN, User.Role.FINANCE_DIRECTOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userFinesBetweenDate(@Context ContainerRequestContext requestContext, @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {

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

            return userFinesBetweenDateSQL(userId, startDateDate, endDateDate);

        } catch (ParseException e) {
            response.put("message", "Failed to parse date: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        }
    }

    public Response userFinesBetweenDateSQL(String userId, Date startDate, Date endDate) {
        HashMap<String, Object> response = new HashMap<>();

        LoanFinesService loanFinesService = new LoanFinesService();
        HashMap<String, Object>[] loanFines = loanFinesService.findFinesForUserBetweenDate(Integer.parseInt(userId), startDate, endDate);

        response.put("loanedBooks", loanFines);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();

    }
}
