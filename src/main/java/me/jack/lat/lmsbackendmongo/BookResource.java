package me.jack.lat.lmsbackendmongo;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.security.Protected;

import java.util.HashMap;
import java.util.Map;

@Path("/books/create")
public class BookResource {

    public static class Book {
        public String title;
        public String author;
    }

    @POST
    @Protected
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBook(Book book) {
        String title = book.title;
        String author = book.author;

        Map<String, String> response = new HashMap<>();
        response.put("title", title);
        response.put("author", author);

        return Response.ok(response).build();
    }
}