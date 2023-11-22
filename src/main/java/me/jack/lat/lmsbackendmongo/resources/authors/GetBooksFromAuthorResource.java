package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.BookAuthor;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.mongoDB.AuthorService;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

@Path("/authors/{authorId}/books")
public class GetBooksFromAuthorResource {

    @GET
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksFromAuthor(@HeaderParam("Database-Type") String databaseType, @PathParam("authorId") String authorId) {

        try {
            new ObjectId(authorId);

        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "No Author found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return getBooksFromAuthorSQL(authorId);
        } else {
            return getBooksFromAuthorMongoDB(authorId);
        }

    }

    public Response getBooksFromAuthorMongoDB(String authorId) {
        Map<String, Object> response = new HashMap<>();

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

    public Response getBooksFromAuthorSQL(String authorId) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Not implemented yet - SQL");

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
