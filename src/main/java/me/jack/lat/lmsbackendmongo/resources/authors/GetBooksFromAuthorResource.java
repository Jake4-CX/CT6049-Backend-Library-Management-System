package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;

import java.util.HashMap;
import java.util.Map;

@Path("/authors/{authorId}/books")
public class GetBooksFromAuthorResource {

    @GET
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksFromAuthor(@PathParam("authorId") String authorId) {
        return getBooksFromAuthorSQL(authorId);

    }

    public Response getBooksFromAuthorSQL(String authorId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer.valueOf(authorId);
        } catch (NumberFormatException e) {
            response.put("message", "Invalid Author id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService authorService = new me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService();

        if (authorService.getAuthorFromId(Integer.parseInt(authorId)) == null) {
            response.put("message", "No Author found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        HashMap<String, Object>[] authorBooks = authorService.getBooksFromAuthor(Integer.parseInt(authorId));

        response.put("books", authorBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
