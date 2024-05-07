package me.jack.lat.lmsbackendmongo.resources.categories;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;

import java.util.HashMap;
import java.util.Map;

@Path("/categories/{categoryId}/books")
public class GetBooksFromCategoryResource {

    @GET
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksFromCategory(@PathParam("categoryId") String categoryId) {

        return getBooksFromCategorySQL(categoryId);

    }

    public Response getBooksFromCategorySQL(String categoryId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer.valueOf(categoryId);
        } catch (NumberFormatException e) {
            response.put("message", "Invalid Category id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService categoryService = new me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService();

        if (categoryService.getCategoryFromId(Integer.valueOf(categoryId)) == null) {
            response.put("message", "No Category found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        HashMap<String, Object>[] categoryBooks = categoryService.getBooksFromCategory(Integer.valueOf(categoryId));

        response.put("books", categoryBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

}
