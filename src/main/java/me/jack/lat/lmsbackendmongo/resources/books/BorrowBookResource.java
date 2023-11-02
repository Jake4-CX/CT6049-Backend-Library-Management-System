package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.service.BookService;
import me.jack.lat.lmsbackendmongo.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Path("/books/{bookId}/borrow")
public class BorrowBookResource {

    @GET
    @RestrictedRoles({User.Role.USER, User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response borrowBook(@PathParam("bookId") String bookId, @Context ContainerRequestContext requestContext) {

        Map<String, Object> response = new HashMap<>();

        // Obtain userId from requestContext.
        String userId = (String) requestContext.getProperty("userId");

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

}
