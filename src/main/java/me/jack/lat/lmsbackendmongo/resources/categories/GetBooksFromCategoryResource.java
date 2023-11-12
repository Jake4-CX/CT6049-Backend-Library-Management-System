package me.jack.lat.lmsbackendmongo.resources.categories;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.BookCategory;
import me.jack.lat.lmsbackendmongo.service.CategoryService;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

@Path("/categories/{categoryId}/books")
public class GetBooksFromCategoryResource {

    @GET
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksFromCategory(@PathParam("categoryId") String categoryId) {

        Map<String, Object> response = new HashMap<>();

        try {
            new ObjectId(categoryId);

        } catch (IllegalArgumentException e) {
            response.put("message", "No Category found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        CategoryService categoryService = new CategoryService();
        BookCategory selectedCategory = categoryService.getCategoryFromId(categoryId);

        if (selectedCategory == null) {
            response.put("message", "No Category found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        Book[] categoryBooks = categoryService.getBooksFromCategory(selectedCategory);

        response.put("books", categoryBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

}
