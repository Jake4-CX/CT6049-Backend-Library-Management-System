package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.model.Book;
import me.jack.lat.lmsbackendmongo.service.BookService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/books")
public class GetBooksResource {

    @GET
    public Response getBooks(@QueryParam("sort") String sort, @QueryParam("filter") String filter, @QueryParam("page") Integer page, @QueryParam("limit") Integer limit) {

//        if (sort == null || sort.isEmpty()) {
//            sort = "";
//        }
//
//        if (filter == null || filter.isEmpty()) {
//            filter = "";
//        }
//
//        if (page < 0) {
//            page = 0;
//        }
//
//        if (limit < 20) {
//            limit = 20;
//        }

        Map<String, Object> response = new HashMap<>();
        BookService bookService = new BookService();

        List<Book> books = bookService.getBooks(sort, filter, page, limit);

        response.put("message", "success");
        response.put("books", books);
        response.put("settings", new HashMap<String, Object>() {{
            put("sort", sort);
            put("filter", filter);
            put("page", page);
            put("limit", limit);
        }}
        );

        return Response.ok(response).build();
    }
}