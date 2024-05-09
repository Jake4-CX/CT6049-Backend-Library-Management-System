package me.jack.lat.lmsbackendmongo.resources.categories;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.model.NewBookCategory;

import java.util.HashMap;
import java.util.Map;

@Path("/categories")
public class CreateCategoryResource {

    @POST
    @RestrictedRoles({User.Role.CHIEF_LIBRARIAN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBookCategory(@Valid NewBookCategory newBookCategory) {

        return createBookCategorySQL(newBookCategory);
    }

    public Response createBookCategorySQL(NewBookCategory newBookCategory) {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService categoryService = new me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService();
        Error categoryCreated = categoryService.createCategory(newBookCategory);

        if (categoryCreated == null) {
            response.put("message", "success");
            return Response.status(Response.Status.CREATED).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", "Category with the same name already exists.");
            return Response.status(Response.Status.CONFLICT).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }
}
