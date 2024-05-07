package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;

import java.util.HashMap;

import java.util.Map;
import java.util.logging.Logger;

@Path("/books")
public class GetBooksResource {

    public static final Logger logger = Logger.getLogger(GetBooksResource.class.getName());

    @GET
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks(@DefaultValue("") @QueryParam("sort") String sort, @DefaultValue("") @QueryParam("filter") String filter, @DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("20") @QueryParam("limit") Integer limit) {

        if (page < 0) {
            page = 0;
        }

        if (limit < 20) {
            limit = 20;
        }

        return getBooksSQL(sort, filter, page, limit);

    }

    public Response getBooksSQL(String sort, String filter, Integer page, Integer limit) {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.BookService bookService = new me.jack.lat.lmsbackendmongo.service.oracleDB.BookService();
        HashMap<String, Object>[] books = bookService.getBooks(sort, filter, page, limit);

        response.put("books", books);

        response.put("settings", new HashMap<String, Object>() {{
            put("sort", sort);
            put("filter", filter);
            put("page", page);
            put("limit", limit);
        }});

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}