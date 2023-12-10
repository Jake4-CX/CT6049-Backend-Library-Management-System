package me.jack.lat.lmsbackendmongo.resources.loanFine;

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
import me.jack.lat.lmsbackendmongo.service.oracleDB.LoanFinesService;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@Path("/users/me/fines/{loanedBookId}/pay")
public class userFinesPayResource {

    private static final Logger logger = Logger.getLogger(userFinesPayResource.class.getName());

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response userFinesPay(@HeaderParam("Database-Type") String databaseType, @Context ContainerRequestContext requestContext, @PathParam("loanedBookId") String loanedBookId) {

        String userId = (String) requestContext.getProperty("userId");

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return userFinesPaySQL(loanedBookId, userId);
        } else {
            return userFinesPayMongoDB(loanedBookId, userId);
        }
    }

    private Response userFinesPayMongoDB(String loanedBookId, String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            new ObjectId(loanedBookId);

        } catch (Exception e) {
            response.put("message", "No loanFine found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        LoanedBookService loanedBookService = new LoanedBookService();
        LoanedBook loanedBook = loanedBookService.getLoanedBookFromId(loanedBookId);

        if (loanedBook.getLoanedBookId() == null) {
            response.put("error", new HashMap<>(){{
                put("message", "LoanBook does not exist");
                put("type", 400);
            }});
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        if (!Objects.equals(loanedBook.getUser().getUserId(), userId)) {
            response.put("error", new HashMap<>(){{
                put("message", "LoanBook does not belong to this user");
                put("type", 403);
            }});
            return Response.status(Response.Status.FORBIDDEN).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        if (loanedBook.getLoanFine() == null) {
            response.put("error", new HashMap<>(){{
                put("message", "LoanFine does not exist");
                put("type", 400);
            }});
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        if (loanedBook.getLoanFine().getPaidAt() != null) {
            response.put("error", new HashMap<>(){{
                put("message", "LoanFine already paid");
                put("type", 400);
            }});
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        if (loanedBookService.payFine(loanedBook)) {
            response.put("message", "Fine paid successfully");
            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("error", new HashMap<>(){{
                put("message", "Error paying fine");
                put("type", 500);
            }});
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }

    private Response userFinesPaySQL(String loanedBookId, String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer.valueOf(loanedBookId);
        } catch (NumberFormatException e) {
            response.put("message", "No loanFine found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        LoanFinesService loanFinesService = new LoanFinesService();
        Error error = loanFinesService.payFine(Integer.valueOf(loanedBookId), Integer.valueOf(userId));

        if (error != null) {
            response.put("error", new HashMap<>(){{
                put("message", error.getMessage());
                put("type", 400);
            }});
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", "Fine paid successfully");
            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }
}
