package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/books/search")
public class SearchBooksResource {


    @GET
    @Path("/{bookName}")
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBooks(@PathParam("bookName") String bookName) {

        return searchBooksSQL(bookName);

    }

    public Response searchBooksSQL(String bookName) {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.BookService bookService = new me.jack.lat.lmsbackendmongo.service.oracleDB.BookService();
        HashMap<String, Object>[] bookResults = bookService.searchBooks(bookName);

        response.put("books", bookResults);

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
