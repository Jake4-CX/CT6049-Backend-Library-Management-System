package me.jack.lat.lmsbackendmongo.resources.categories;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.BookCategory;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.mongoDB.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/categories")
public class GetCategoriesResource {

    @GET
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories(@HeaderParam("Database-Type") String databaseType, @DefaultValue("") @QueryParam("sort") String sort, @DefaultValue("") @QueryParam("filter") String filter, @DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("20") @QueryParam("limit") Integer limit) {

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
            return getCategoriesSQL();
        } else {
            return getCategoriesMongoDB(sort, filter, page, limit);
        }

    }

    public Response getCategoriesMongoDB(String sort, String filter, Integer page, Integer limit) {
        Map<String, Object> response = new HashMap<>();
        CategoryService categoryService = new CategoryService();

        List<BookCategory> bookCategories = categoryService.getCategories(sort, filter, page, limit);

        response.put("categories", bookCategories);

        response.put("settings", new HashMap<String, Object>() {{
            put("sort", sort);
            put("filter", filter);
            put("page", page);
            put("limit", limit);
        }});

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

    public Response getCategoriesSQL() {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService categoryService = new me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService();
        HashMap<String, Object>[] categories = categoryService.getCategories();

        response.put("categories", categories);


        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
