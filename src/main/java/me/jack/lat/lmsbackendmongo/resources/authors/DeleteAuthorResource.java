package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService;

import java.util.HashMap;
import java.util.Map;

@Path("/authors/{authorId}/delete")
public class DeleteAuthorResource {

    @DELETE
    @RestrictedRoles({User.Role.CHIEF_LIBRARIAN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBookAuthor(@PathParam("authorId") String authorId) {
        return deleteBookAuthorSQL(authorId);
    }

    public Response deleteBookAuthorSQL(String authorId) {
        Map<String, Object> response = new HashMap<>();

        AuthorService authorService = new AuthorService();

        try {
            Error authorDeleted = authorService.deleteAuthor(Integer.valueOf(authorId));

            if (authorDeleted == null) {
                response.put("message", "success");
                return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
            } else {
                response.put("error", new HashMap<>(){{
                    put("message", authorDeleted.getMessage());
                    put("type", 400);
                }});
                return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
            }

        } catch (NumberFormatException e) {
            response.put("error", new HashMap<>(){{
                put("message", "Invalid authorId");
                put("type", 400);
            }});
            return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }
}