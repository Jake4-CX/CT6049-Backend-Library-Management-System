package me.jack.lat.lmsbackendmongo.resources.books;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.LoanedBook;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.mongoDB.BookService;
import me.jack.lat.lmsbackendmongo.service.mongoDB.LoanedBookService;
import me.jack.lat.lmsbackendmongo.service.mongoDB.UserService;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

@Path("/books/{bookId}")
public class GetBookResource {

    @GET
    @UnprotectedRoute
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBook(@HeaderParam("Database-Type") String databaseType, @PathParam("bookId") String bookId, @HeaderParam("Authorization") String authorizationHeader) {

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return getBookSQL(bookId, authorizationHeader);
        } else {
            return getBookMongoDB(bookId, authorizationHeader);
        }

    }

    public Response getBookMongoDB(String bookId, String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            new ObjectId(bookId);

        } catch (Exception e) {
            // BookId is not a valid ObjectId (24 character hex string). Return 404.

            response.put("message", "Invalid Book id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        BookService bookService = new BookService();
        Book selectedBook = bookService.getBookFromId(bookId);

        if (selectedBook != null) {
            response.put("book", selectedBook);

            LoanedBookService loanedBookService = new LoanedBookService();

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String authorizationToken = authorizationHeader.substring("Bearer".length()).trim();

                UserService userService = new UserService();
                User userEntity = userService.validateAccessToken(authorizationToken);

                if (userEntity != null) {
                    LoanedBook loanedBook = loanedBookService.findActiveLoanForBookAndUser(selectedBook, userEntity);

                    if (loanedBook != null) {
                        loanedBook.setUser(null);
                        loanedBook.setBook(null);

                        response.put("loanedBook", loanedBook);
                    }
                }

            }

            response.put("booksLoaned", loanedBookService.getUnreturnedBooksWithBook(selectedBook).size());

            response.put("message", "success");
            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", "No Book found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

    }

    public Response getBookSQL(String bookId, String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer.valueOf(bookId);
        } catch (NumberFormatException e) {
            response.put("message", "Invalid Book id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        me.jack.lat.lmsbackendmongo.service.oracleDB.BookService bookService = new me.jack.lat.lmsbackendmongo.service.oracleDB.BookService();

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String authorizationToken = authorizationHeader.substring("Bearer".length()).trim();
            me.jack.lat.lmsbackendmongo.service.oracleDB.UserService userService = new me.jack.lat.lmsbackendmongo.service.oracleDB.UserService();
            HashMap<String, Object> userEntity = userService.validateAccessToken(authorizationToken);

            if (userEntity == null) {
                response.put("message", "Invalid access token.");
                return Response.status(Response.Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON).build();

            } else {

                try {
                    HashMap <String, Object> book = bookService.getBookFromId(Integer.valueOf(bookId), (Integer) userEntity.get("userId"));

                    if (book != null) {
                        return Response.status(Response.Status.OK).entity(book).type(MediaType.APPLICATION_JSON).build();
                    } else {
                        response.put("message", "No Book found with this id");
                        return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
                    }

                } catch (NumberFormatException e) {

                    response.put("message", "No Book found with this id");
                    return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
                }
            }

        } else {

            try {
                HashMap<String, Object> book = bookService.getBookFromId(Integer.valueOf(bookId));

                if (book != null) {
                    return Response.status(Response.Status.OK).entity(book).type(MediaType.APPLICATION_JSON).build();
                } else {
                    response.put("message", "No Book found with this id");
                    return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
                }

            } catch (NumberFormatException e) {

                response.put("message", "No Book found with this id");
                return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
            }

        }

    }
}
