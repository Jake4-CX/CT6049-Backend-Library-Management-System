package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.model.NewBook;

import java.util.HashMap;
import java.util.Map;

@Path("/books")
public class CreateBookResource {

    @POST
    @RestrictedRoles({User.Role.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBook(@Valid NewBook newBook) {
        return createBookSQL(newBook);
    }

    public Response createBookSQL(NewBook newBook) {
        Map<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.BookService bookService = new me.jack.lat.lmsbackendmongo.service.oracleDB.BookService();
        Error bookCreated = bookService.createBook(newBook);

        if (bookCreated == null) {
            response.put("message", "success");
            return Response.status(Response.Status.CREATED).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", bookCreated.getMessage());
            return Response.status(Response.Status.CONFLICT).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }
}