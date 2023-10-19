package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.model.NewBookAuthor;
import me.jack.lat.lmsbackendmongo.service.AuthorService;

import java.util.HashMap;
import java.util.Map;

@Path("/authors")
public class CreateAuthorResource {

    @POST
    @RestrictedRoles("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBookAuthor(NewBookAuthor newBookAuthor) {

        Map<String, Object> response = new HashMap<>();

        AuthorService authorService = new AuthorService();
        boolean isAuthorCreated = authorService.createAuthor(newBookAuthor);

        if (isAuthorCreated) {
            response.put("message", "success");
            return Response.status(Response.Status.CREATED).entity(response).build();
        } else {
            response.put("message", "Author with the same name already exists.");
            return Response.status(Response.Status.CONFLICT).entity(response).build();
        }

    }
}
