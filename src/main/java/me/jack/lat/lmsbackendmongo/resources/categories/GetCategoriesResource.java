package me.jack.lat.lmsbackendmongo.resources.categories;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;

import java.util.HashMap;
import java.util.Map;

@Path("/categories")
public class GetCategoriesResource {

    @GET
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories() {
        return getCategoriesSQL();
    }


    public Response getCategoriesSQL() {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService categoryService = new me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService();
        HashMap<String, Object>[] categories = categoryService.getCategories();

        response.put("categories", categories);


        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
