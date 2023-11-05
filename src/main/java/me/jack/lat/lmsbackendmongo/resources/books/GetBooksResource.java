package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.LoanedBook;
import me.jack.lat.lmsbackendmongo.service.BookService;
import me.jack.lat.lmsbackendmongo.service.LoanedBookService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/books")
public class GetBooksResource {

    @GET
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks(@DefaultValue("") @QueryParam("sort") String sort, @DefaultValue("") @QueryParam("filter") String filter, @DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("20") @QueryParam("limit") Integer limit) {

        if (page < 0) {
            page = 0;
        }

        if (limit < 20) {
            limit = 20;
        }

        Map<String, Object> response = new HashMap<>();
        BookService bookService = new BookService();
        LoanedBookService loanedBookService = new LoanedBookService();

        List<Book> books = bookService.getBooks(sort, filter, page, limit);

        List<Object> booksObject = new ArrayList<>();

        books.forEach((book) -> {
            List<LoanedBook> loanedBooks = loanedBookService.getUnreturnedBooksWithBook(book);
            Map<String, Object> bookObject = new HashMap<>();
            bookObject.put("book", book);
            bookObject.put("booksLoaned", loanedBooks.size());

            booksObject.add(bookObject);
        });

        response.put("books", booksObject);

        final String finalSort = sort;
        final String finalFilter = filter;
        final int finalPage = page;
        final int finalLimit = limit;

        response.put("settings", new HashMap<String, Object>() {{
            put("sort", finalSort);
            put("filter", finalFilter);
            put("page", finalPage);
            put("limit", finalLimit);
        }});

        return Response.ok(response).build();
    }
}