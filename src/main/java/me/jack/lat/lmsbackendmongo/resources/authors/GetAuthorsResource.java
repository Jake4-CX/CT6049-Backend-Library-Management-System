package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.BookAuthor;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.mongoDB.AuthorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/authors")
public class GetAuthorsResource {

    @GET
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthors(@HeaderParam("Database-Type") String databaseType, @DefaultValue("") @QueryParam("sort") String sort, @DefaultValue("") @QueryParam("filter") String filter, @DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("20") @QueryParam("limit") Integer limit) {

        if (page < 0) {
            page = 0;
        }

        if (limit < 20) {
            limit = 20;
        }

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return getAuthorsSQL();
        } else {
            return getAuthorsMongoDB(sort, filter, page, limit);
        }

    }

    public Response getAuthorsMongoDB(String sort, String filter, Integer page, Integer limit) {
        Map<String, Object> response = new HashMap<>();
        AuthorService authorService = new AuthorService();

        List<BookAuthor> bookAuthors = authorService.getAuthors(sort, filter, page, limit);

        response.put("authors", bookAuthors);

        response.put("settings", new HashMap<String, Object>() {{
            put("sort", sort);
            put("filter", filter);
            put("page", page);
            put("limit", limit);
        }});

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

    public Response getAuthorsSQL() {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService authorService = new me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService();
        HashMap<String, Object>[] bookAuthors = authorService.getAuthors();

        response.put("authors", bookAuthors);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
