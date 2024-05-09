package me.jack.lat.lmsbackendmongo.resources.categories;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService;

import java.util.HashMap;
import java.util.Map;

@Path("/categories/{categoryId}/recover")
public class RecoverCategoryResource {

    @GET
    @RestrictedRoles({User.Role.CHIEF_LIBRARIAN})
    public Response recoverCategory(@PathParam("categoryId") String categoryId) {
        return recoverCategorySQL(categoryId);
    }

    public Response recoverCategorySQL(String categoryId) {
        Map<String, Object> response = new HashMap<>();

        CategoryService categoryService = new CategoryService();

        try {
            Error categoryRecovered = categoryService.recoverCategory(Integer.valueOf(categoryId));

            if (categoryRecovered == null) {
                response.put("message", "success");
                return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
            } else {
                response.put("error", new HashMap<>(){{
                    put("message", categoryRecovered.getMessage());
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
