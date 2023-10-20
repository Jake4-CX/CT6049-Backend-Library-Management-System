package me.jack.lat.lmsbackendmongo.resources.categories;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.BookCategory;
import me.jack.lat.lmsbackendmongo.service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/categories")
public class GetCategoriesResource {

    @GET
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories(@DefaultValue("") @QueryParam("sort") String sort, @DefaultValue("") @QueryParam("filter") String filter, @DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("20") @QueryParam("limit") Integer limit) {

        if (page < 0) {
            page = 0;
        }

        if (limit < 20) {
            limit = 20;
        }

        Map<String, Object> response = new HashMap<>();
        CategoryService categoryService = new CategoryService();

        List<BookCategory> bookCategories = categoryService.getCategories(sort, filter, page, limit);

        response.put("categories", bookCategories);

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
