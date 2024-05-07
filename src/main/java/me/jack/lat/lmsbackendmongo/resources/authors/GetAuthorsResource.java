package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;

import java.util.HashMap;
import java.util.Map;

@Path("/authors")
public class GetAuthorsResource {

    @GET
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthors() {
        return getAuthorsSQL();

    }

    public Response getAuthorsSQL() {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService authorService = new me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService();
        HashMap<String, Object>[] bookAuthors = authorService.getAuthors();

        response.put("authors", bookAuthors);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
