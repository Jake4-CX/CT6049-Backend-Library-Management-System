package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService;

import java.util.HashMap;
import java.util.Map;

@Path("/authors/{authorId}/recover")
public class RecoverAuthorResource {

    @GET
    @RestrictedRoles({User.Role.CHIEF_LIBRARIAN})
    public Response recoverAuthor(@PathParam("authorId") String authorId) {
        return recoverAuthorSQL(authorId);
    }

    public Response recoverAuthorSQL(String authorId) {
        Map<String, Object> response = new HashMap<>();

        AuthorService authorService = new AuthorService();

        try {
            Error authorRecovered = authorService.recoverAuthor(Integer.valueOf(authorId));

            if (authorRecovered == null) {
                response.put("message", "success");
                return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
            } else {
                response.put("error", new HashMap<>(){{
                    put("message", authorRecovered.getMessage());
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
