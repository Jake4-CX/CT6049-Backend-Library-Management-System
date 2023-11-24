package me.jack.lat.lmsbackendmongo.resources.authors;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.BookAuthor;
import me.jack.lat.lmsbackendmongo.entities.LoanedBook;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.mongoDB.AuthorService;
import me.jack.lat.lmsbackendmongo.service.mongoDB.LoanedBookService;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/authors/{authorId}/books")
public class GetBooksFromAuthorResource {

    @GET
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksFromAuthor(@HeaderParam("Database-Type") String databaseType, @PathParam("authorId") String authorId) {

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return getBooksFromAuthorSQL(authorId);
        } else {
            return getBooksFromAuthorMongoDB(authorId);
        }

    }

    public Response getBooksFromAuthorMongoDB(String authorId) {
        Map<String, Object> response = new HashMap<>();

        try {
            new ObjectId(authorId);

        } catch (IllegalArgumentException e) {
            response.put("message", "Invalid Author id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        AuthorService authorService = new AuthorService();
        LoanedBookService loanedBookService = new LoanedBookService();

        BookAuthor selectedAuthor = authorService.getAuthorFromId(authorId);

        if (selectedAuthor == null) {
            response.put("message", "No Author found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        List<Book> authorBooks = authorService.getBooksFromAuthor(selectedAuthor);
        List<Object> booksObject = new ArrayList<>();

        authorBooks.forEach((book) -> {
            List<LoanedBook> loanedBooks = loanedBookService.getUnreturnedBooksWithBook(book);
            Map<String, Object> bookObject = new HashMap<>();
            bookObject.put("book", book);
            bookObject.put("booksLoaned", loanedBooks.size());

            booksObject.add(bookObject);
        });

        response.put("books", booksObject);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

    public Response getBooksFromAuthorSQL(String authorId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer.valueOf(authorId);
        } catch (NumberFormatException e) {
            response.put("message", "Invalid Author id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService authorService = new me.jack.lat.lmsbackendmongo.service.oracleDB.AuthorService();

        if (authorService.getAuthorFromId(Integer.parseInt(authorId)) == null) {
            response.put("message", "No Author found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        HashMap<String, Object>[] authorBooks = authorService.getBooksFromAuthor(Integer.parseInt(authorId));

        response.put("books", authorBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
