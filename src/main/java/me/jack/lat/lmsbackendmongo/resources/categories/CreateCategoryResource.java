package me.jack.lat.lmsbackendmongo.resources.categories;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.model.NewBookCategory;
import me.jack.lat.lmsbackendmongo.service.CategoryService;

import java.util.HashMap;
import java.util.Map;

@Path("/categories")
public class CreateCategoryResource {

    @POST
    @RestrictedRoles("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBookCategory(@Valid NewBookCategory newBookCategory) {

        Map<String, Object> response = new HashMap<>();

        CategoryService categoryService = new CategoryService();
        boolean isCategoryCreated = categoryService.createCategory(newBookCategory);

        if (isCategoryCreated) {
            response.put("message", "success");
            return Response.status(Response.Status.CREATED).entity(response).build();
        } else {
            response.put("message", "Category with the same name already exists.");
            return Response.status(Response.Status.CONFLICT).entity(response).build();
        }

    }
}
