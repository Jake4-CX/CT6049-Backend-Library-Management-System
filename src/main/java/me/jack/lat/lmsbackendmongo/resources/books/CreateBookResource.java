package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.model.Book;
import me.jack.lat.lmsbackendmongo.service.BookService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/books")
public class CreateBookResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBook() {

        Map<String, String> response = new HashMap<>();

        return Response.ok(response).build();
    }
}