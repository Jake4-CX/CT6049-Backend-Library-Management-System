package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.service.BookService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/books/search/{bookName}")
public class SearchBooksResource {


    @GET
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBooks(@PathParam("bookName") String bookName) {

        BookService bookService = new BookService();
        List<Book> bookResults = bookService.searchBooks(bookName);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> settings = new HashMap<>();

        settings.put("search", bookName);

        response.put("settings", settings);
        response.put("books", bookResults);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
