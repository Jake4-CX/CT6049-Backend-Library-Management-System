package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.mongoDB.BookService;
import me.jack.lat.lmsbackendmongo.service.mongoDB.UserService;

import java.util.HashMap;
import java.util.Map;

@Path("/books/{bookId}/borrow")
public class BorrowBookResource {

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response borrowBook(@HeaderParam("Database-Type") String databaseType, @PathParam("bookId") String bookId, @Context ContainerRequestContext requestContext) {

        // Obtain userId from requestContext.
        String userId = (String) requestContext.getProperty("userId");

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return borrowBookSQL(bookId, userId);
        } else {
            return borrowBookMongoDB(bookId, userId);
        }

    }

    public Response borrowBookMongoDB(String bookId, String userId) {
        Map<String, Object> response = new HashMap<>();

        UserService userService = new UserService();
        User user = userService.findUserById(userId);

        BookService bookService = new BookService();

        Error error;
        if ((error = bookService.borrowBook(bookId, user)) == null) {
            // Book was successfully borrowed.
            response.put("message", "success");
            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            // Book was not successfully borrowed.
            response.put("error", new HashMap<>(){{
                put("message", error.getMessage());
                put("type", 400);
            }});
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        }
    }

    public Response borrowBookSQL(String bookId, String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Not implemented yet - SQL");

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

}
