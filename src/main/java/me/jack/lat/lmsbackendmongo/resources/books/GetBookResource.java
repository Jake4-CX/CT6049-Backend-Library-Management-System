package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.service.BookService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/books/{bookId}")
public class GetBookResource {

    @GET
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBook(@PathParam("bookId") String bookId) {

        // ToDo: Check if book exists with given bookId.

        BookService bookService = new BookService();
        Book selectedBook = bookService.getBookFromId(bookId);

        Map<String, Object> response = new HashMap<>();
        response.put("bookId", bookId);

        if (selectedBook != null) {
            response.put("book", selectedBook);
            response.put("message", "success");
            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", "No Book found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }
}
