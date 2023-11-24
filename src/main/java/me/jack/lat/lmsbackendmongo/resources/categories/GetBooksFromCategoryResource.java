package me.jack.lat.lmsbackendmongo.resources.categories;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.BookCategory;
import me.jack.lat.lmsbackendmongo.entities.LoanedBook;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.mongoDB.CategoryService;
import me.jack.lat.lmsbackendmongo.service.mongoDB.LoanedBookService;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/categories/{categoryId}/books")
public class GetBooksFromCategoryResource {

    @GET
    @UnprotectedRoute
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksFromCategory(@HeaderParam("Database-Type") String databaseType, @PathParam("categoryId") String categoryId) {

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return getBooksFromCategorySQL(categoryId);
        } else {
            return getBooksFromCategoryMongoDB(categoryId);
        }

    }

    public Response getBooksFromCategoryMongoDB(String categoryId) {
        Map<String, Object> response = new HashMap<>();

        try {
            new ObjectId(categoryId);

        } catch (IllegalArgumentException e) {
            response.put("message", "Invalid Category id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        CategoryService categoryService = new CategoryService();
        LoanedBookService loanedBookService = new LoanedBookService();

        BookCategory selectedCategory = categoryService.getCategoryFromId(categoryId);

        if (selectedCategory == null) {
            response.put("message", "No Category found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        List<Book> categoryBooks = categoryService.getBooksFromCategory(selectedCategory);
        List<Object> booksObject = new ArrayList<>();

        categoryBooks.forEach((book) -> {
            List<LoanedBook> loanedBooks = loanedBookService.getUnreturnedBooksWithBook(book);
            Map<String, Object> bookObject = new HashMap<>();
            bookObject.put("book", book);
            bookObject.put("booksLoaned", loanedBooks.size());

            booksObject.add(bookObject);
        });

        response.put("books", booksObject);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

    public Response getBooksFromCategorySQL(String categoryId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer.valueOf(categoryId);
        } catch (NumberFormatException e) {
            response.put("message", "Invalid Category id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService categoryService = new me.jack.lat.lmsbackendmongo.service.oracleDB.CategoryService();

        if (categoryService.getCategoryFromId(Integer.valueOf(categoryId)) == null) {
            response.put("message", "No Category found with this id");
            return Response.status(Response.Status.NOT_FOUND).entity(response).type(MediaType.APPLICATION_JSON).build();
        }

        HashMap<String, Object>[] categoryBooks = categoryService.getBooksFromCategory(Integer.valueOf(categoryId));

        response.put("books", categoryBooks);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

}
