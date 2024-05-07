package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;

import java.util.HashMap;
import java.util.Map;

@Path("/books/{bookId}/borrow")
public class BorrowBookResource {

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response borrowBook(@PathParam("bookId") String bookId, @Context ContainerRequestContext requestContext) {

        // Obtain userId from requestContext.
        String userId = (String) requestContext.getProperty("userId");
        return borrowBookSQL(bookId, userId);

    }

    public Response borrowBookSQL(String bookId, String userId) {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.BookService bookService = new me.jack.lat.lmsbackendmongo.service.oracleDB.BookService();

        try {
            Error error = bookService.borrowBook(Integer.valueOf(bookId), Integer.valueOf(userId));

            if (error == null) {
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

        } catch (NumberFormatException e) {
            response.put("error", new HashMap<>(){{
                put("message", "Invalid bookId");
                put("type", 400);
            }});
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }

}
