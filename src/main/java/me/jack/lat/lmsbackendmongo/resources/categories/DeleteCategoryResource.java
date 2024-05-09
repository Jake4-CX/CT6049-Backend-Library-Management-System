package me.jack.lat.lmsbackendmongo.resources.categories;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService;

import java.util.HashMap;
import java.util.Map;

@Path("/categories/{categoryId}/delete")
public class DeleteCategoryResource {

    @DELETE
    @RestrictedRoles({User.Role.CHIEF_LIBRARIAN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCategory(@PathParam("categoryId") String categoryId) {
        return deleteCategorySQL(categoryId);
    }

    public Response deleteCategorySQL(String categoryId) {
        Map<String, Object> response = new HashMap<>();

        CategoryService categoryService = new CategoryService();

        try {
            Error categoryDeleted = categoryService.deleteCategory(Integer.valueOf(categoryId));

            if (categoryDeleted == null) {
                response.put("message", "success");
                return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
            } else {
                response.put("error", new HashMap<>(){{
                    put("message", categoryDeleted.getMessage());
                    put("type", 400);
                }});
                return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
            }

        } catch (NumberFormatException e) {
            response.put("error", new HashMap<>(){{
                put("message", "Invalid categoryId");
                put("type", 400);
            }});
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        }
    }
}
