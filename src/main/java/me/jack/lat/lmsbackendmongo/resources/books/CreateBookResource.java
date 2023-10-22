package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.model.NewBook;
import me.jack.lat.lmsbackendmongo.service.BookService;

import java.util.HashMap;
import java.util.Map;

@Path("/books")
public class CreateBookResource {

    @POST
    @RestrictedRoles(User.Role.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBook(@Valid NewBook newBook) {

        Map<String, Object> response = new HashMap<>();

         BookService bookService = new BookService();
         boolean isBookCreated = bookService.createBook(newBook);

        if (isBookCreated) {
            response.put("message", "success");
            return Response.status(Response.Status.CREATED).entity(response).build();
        } else {
            response.put("message", "Book with the same name already exists.");
            return Response.status(Response.Status.CONFLICT).entity(response).build();
        }

    }
}