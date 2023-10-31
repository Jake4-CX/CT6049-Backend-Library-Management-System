package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.service.BookService;

@Path("/books/borrow/{bookId}")
public class BorrowBookResource {

    @GET
    @RestrictedRoles(User.Role.USER)
    @Produces(MediaType.APPLICATION_JSON)
    public Response borrowBook(@PathParam("bookId") String bookId) {

        // ToDo: get submitting user's User object.

        BookService bookService = new BookService();
        bookService.borrowBook(bookId);
    }

}
