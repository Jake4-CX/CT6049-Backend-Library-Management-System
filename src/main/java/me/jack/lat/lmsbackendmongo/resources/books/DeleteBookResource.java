package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.service.oracleDB.BookService;

import java.util.HashMap;
import java.util.Map;

@Path("/books/{bookId}/delete")
public class DeleteBookResource {

    @DELETE
    @RestrictedRoles({User.Role.CHIEF_LIBRARIAN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBook(@PathParam("bookId") String bookId) {
        return deleteBookSQL(bookId);
    }

    public Response deleteBookSQL(String bookId) {
        Map<String, Object> response = new HashMap<>();

        BookService bookService = new BookService();

        try {
            Error bookDeleted = bookService.deleteBook(Integer.valueOf(bookId));

            if (bookDeleted == null) {
                response.put("message", "success");
                return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
            } else {
                response.put("error", new HashMap<>(){{
                    put("message", bookDeleted.getMessage());
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