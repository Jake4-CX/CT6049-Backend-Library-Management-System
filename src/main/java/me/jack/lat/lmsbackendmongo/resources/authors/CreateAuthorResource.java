package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.model.NewBookAuthor;
import me.jack.lat.lmsbackendmongo.service.mongoDB.AuthorService;

import java.util.HashMap;
import java.util.Map;

@Path("/authors")
public class CreateAuthorResource {

    @POST
    @RestrictedRoles({User.Role.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBookAuthor(@HeaderParam("Database-Type") String databaseType, @Valid NewBookAuthor newBookAuthor) {

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return createBookAuthorSQL(newBookAuthor);
        } else {
            return createBookAuthorMongoDB(newBookAuthor);
        }

    }

    public Response createBookAuthorMongoDB(NewBookAuthor newBookAuthor) {
        Map<String, Object> response = new HashMap<>();

        AuthorService authorService = new AuthorService();
        boolean isAuthorCreated = authorService.createAuthor(newBookAuthor);

        if (isAuthorCreated) {
            response.put("message", "success");
            return Response.status(Response.Status.CREATED).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", "Author with the same name already exists.");
            return Response.status(Response.Status.CONFLICT).entity(response).type(MediaType.APPLICATION_JSON).build();
        }
    }

    public Response createBookAuthorSQL(NewBookAuthor newBookAuthor) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Not implemented yet - SQL");

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
