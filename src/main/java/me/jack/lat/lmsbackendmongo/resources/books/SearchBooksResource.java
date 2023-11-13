package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.BookService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/books/search")
public class SearchBooksResource {


    @GET
    @Path("/{bookName}")
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBooks(@HeaderParam("Database-Type") String databaseType, @PathParam("bookName") String bookName) {

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return searchBooksSQL(bookName);
        } else {
            return searchBooksMongoDB(bookName);
        }

    }

    public Response searchBooksMongoDB(String bookName) {

        BookService bookService = new BookService();
        List<Book> bookResults = bookService.searchBooks(bookName);

        Map<String, Object> response = new HashMap<>();

        response.put("books", bookResults);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

    public Response searchBooksSQL(String bookName) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Not implemented yet - SQL");
        response.put("books", new List[] {});

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBooksFallback() {
        Map<String, Object> response = new HashMap<>();

        response.put("books", new List[] {});

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
