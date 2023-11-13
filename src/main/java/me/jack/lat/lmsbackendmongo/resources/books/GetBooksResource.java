package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.LoanedBook;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks(@HeaderParam("Database-Type") String databaseType, @DefaultValue("") @QueryParam("sort") String sort, @DefaultValue("") @QueryParam("filter") String filter, @DefaultValue("0") @QueryParam("page") Integer page, @DefaultValue("20") @QueryParam("limit") Integer limit) {

        if (page < 0) {
            page = 0;
        }

        if (limit < 20) {
            limit = 20;
        }

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return getBooksSQL(sort, filter, page, limit);
        } else {
            return getBooksMongoDB(sort, filter, page, limit);
        }

    }

    public Response getBooksMongoDB(String sort, String filter, Integer page, Integer limit) {

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

        response.put("settings", new HashMap<String, Object>() {{
            put("sort", sort);
            put("filter", filter);
            put("page", page);
            put("limit", limit);
        }});

        return Response.ok(response).type(MediaType.APPLICATION_JSON).build();
    }

    public Response getBooksSQL(String sort, String filter, Integer page, Integer limit) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Not implemented yet - SQL");
        response.put("books", new List[]{});

        return Response.ok(response).type(MediaType.APPLICATION_JSON).build();
    }
}