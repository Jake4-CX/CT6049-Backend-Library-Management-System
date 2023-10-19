package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.BookAuthor;
import me.jack.lat.lmsbackendmongo.service.AuthorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/authors")
public class GetAuthorsResource {

    @GET
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthors(@DefaultValue("") @QueryParam("sort") String sort, @DefaultValue("") @QueryParam("filter") String filter, @DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("20") @QueryParam("limit") Integer limit) {

        if (page < 0) {
            page = 0;
        }

        if (limit < 20) {
            limit = 20;
        }

        Map<String, Object> response = new HashMap<>();
        AuthorService authorService = new AuthorService();

        List<BookAuthor> bookAuthors = authorService.getAuthors(sort, filter, page, limit);

        response.put("authors", bookAuthors);

        final String finalSort = sort;
        final String finalFilter = filter;
        final int finalPage = page;
        final int finalLimit = limit;

        response.put("settings", new HashMap<String, Object>() {{
            put("sort", finalSort);
            put("filter", finalFilter);
            put("page", finalPage);
            put("limit", finalLimit);
        }});

        return Response.ok(response).build();
    }
}
