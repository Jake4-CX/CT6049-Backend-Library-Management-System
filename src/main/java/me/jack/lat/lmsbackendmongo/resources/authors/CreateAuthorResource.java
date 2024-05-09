package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.model.NewBookAuthor;

import java.util.HashMap;
import java.util.Map;

@Path("/authors")
public class CreateAuthorResource {

    @POST
    @RestrictedRoles({User.Role.CHIEF_LIBRARIAN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBookAuthor(@Valid NewBookAuthor newBookAuthor) {

        return createBookAuthorSQL(newBookAuthor);

    }

    public Response createBookAuthorSQL(NewBookAuthor newBookAuthor) {
        Map<String, Object> response = new HashMap<>();
        me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService authorService = new me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService();
        Error authorCreated = authorService.createAuthor(newBookAuthor);

        if (authorCreated == null) {
            response.put("message", "success");
            return Response.status(Response.Status.CREATED).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", authorCreated.getMessage());
            return Response.status(Response.Status.CONFLICT).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }
}
