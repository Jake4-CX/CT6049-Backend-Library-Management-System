package me.jack.lat.lmsbackendmongo.resources.bookLoans;

import io.github.cdimascio.dotenv.Dotenv;
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
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Path("/loans/{loanId}/pay-fine")
public class PayFineBookLoanResource {

    private static final Dotenv dotenv = Dotenv.configure().load();

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response payFineBookLoan(@HeaderParam("Database-Type") String databaseType, @PathParam("loanId") String loanId, @Context ContainerRequestContext requestContext) {

        String userId = (String) requestContext.getProperty("userId");

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return payFineBookLoanSQL(loanId, userId);
        } else {
            return payFineBookLoanMongoDB(loanId, userId);
        }
    }

    public Response payFineBookLoanMongoDB(String loanId, String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            new ObjectId(loanId);

        } catch (Exception e) {
            response.put("message", "No LoanBook found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        LoanedBookService loanedBookService = new LoanedBookService();
        LoanedBook loanedBook = loanedBookService.getLoanedBookFromId(loanId);

        if (!Objects.equals(loanedBook.getUser().getUserId(), userId)) {
            response.put("error", new HashMap<>(){{
                put("message", "LoanBook does not belong to this user");
                put("type", 403);
            }});
            return Response.status(Response.Status.FORBIDDEN).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        if (loanedBook.getReturnedAt() != null) {
            response.put("error", new HashMap<>(){{
                put("message", "LoanBook already returned");
                put("type", 409);
            }});
            return Response.status(Response.Status.CONFLICT).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        if (!(loanedBookService.isOverdue(loanedBook))) {
            response.put("error", new HashMap<>(){{
                put("message", "LoanBook is not overdue");
                put("type", 409);
            }});
            return Response.status(Response.Status.CONFLICT).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        loanedBook.setReturnedAt(new Date());
        loanedBook.setFinePaid(new LoanedBook.FinePaid(Double.parseDouble(dotenv.get("LATE_FINE_PER_DAY")) * loanedBookService.getDaysOverdue(loanedBook)));

        if (loanedBookService.updateLoanedBook(loanedBook)) {
            response.put("message", "success");
            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("error", new HashMap<>(){{
                put("message", "Failed to update loanedBook");
                put("type", 500);
            }});
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON).build();
        }
    }

    public Response payFineBookLoanSQL(String loanId, String userId) {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.LoanedBookService loanedBookService = new me.jack.lat.lmsbackendmongo.service.oracleDB.LoanedBookService();
        HashMap<String, Object> loanedBook = loanedBookService.getLoanedBookFromId(Integer.valueOf(loanId));

        if (loanedBook == null) {
            response.put("error", new HashMap<>(){{
                put("message", "No LoanBook found with this id");
                put("type", 404);
            }});
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        if (((int) loanedBook.get("userId") != Integer.parseInt(userId))) {
            response.put("error", new HashMap<>(){{
                put("message", "LoanBook does not belong to this user");
                put("type", 403);
            }});
            return Response.status(Response.Status.FORBIDDEN).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        if (loanedBook.get("returnedAt") != null) {
            response.put("error", new HashMap<>(){{
                put("message", "LoanBook already returned");
                put("type", 409);
            }});
            return Response.status(Response.Status.CONFLICT).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        if (!(loanedBookService.isOverdue(loanedBook))) {
            response.put("error", new HashMap<>(){{
                put("message", "LoanBook is not overdue");
                put("type", 409);
            }});
            return Response.status(Response.Status.CONFLICT).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        Error error = loanedBookService.returnAndPayFine(Integer.valueOf(loanId), Integer.valueOf(userId));

        if (error != null) {
            response.put("error", new HashMap<>(){{
                put("message", error.getMessage());
                put("type", 400);
            }});
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", "success");
            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }
}
