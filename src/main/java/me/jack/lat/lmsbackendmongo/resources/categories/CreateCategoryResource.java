package me.jack.lat.lmsbackendmongo.resources.categories;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.model.NewBookCategory;
import me.jack.lat.lmsbackendmongo.service.CategoryService;

import java.util.HashMap;
import java.util.Map;

@Path("/categories")
public class CreateCategoryResource {

    @POST
    @RestrictedRoles({User.Role.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBookCategory(@HeaderParam("Database-Type") String databaseType, @Valid NewBookCategory newBookCategory) {

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return createBookCategorySQL(newBookCategory);
        } else {
            return createBookCategoryMongoDB(newBookCategory);
        }

    }

    public Response createBookCategoryMongoDB(NewBookCategory newBookCategory) {
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

    public Response createBookCategorySQL(NewBookCategory newBookCategory) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Not implemented yet - SQL");

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
