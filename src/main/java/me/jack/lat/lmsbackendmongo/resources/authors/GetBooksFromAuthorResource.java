package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.BookAuthor;
import me.jack.lat.lmsbackendmongo.service.AuthorService;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

@Path("/authors/{authorId}/books")
public class GetBooksFromAuthorResource {

    @GET
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksFromAuthor(@PathParam("authorId") String authorId) {

        Map<String, Object> response = new HashMap<>();

        try {
            new ObjectId(authorId);

        } catch (IllegalArgumentException e) {
            response.put("message", "No Author found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        AuthorService authorService = new AuthorService();
        BookAuthor selectedAuthor = authorService.getAuthorFromId(authorId);

        if (selectedAuthor == null) {
            response.put("message", "No Author found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        Book[] authorBooks = authorService.getBooksFromAuthor(selectedAuthor);

        response.put("books", authorBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
