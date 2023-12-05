package me.jack.lat.lmsbackendmongo.resources.statistics;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.mongoDB.StatisticsService;

import java.util.HashMap;

@Path("/statistics/admin/book-category")
public class AdminStatisticsBookCategoryResource {

    @GET
    @RestrictedRoles({User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response adminStatisticsBookCategory(@HeaderParam("Database-Type") String databaseType) {

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return adminStatisticsBookCategorySQL();
        } else {
            return adminStatisticsBookCategoryMongoDB();
        }
    }

    private Response adminStatisticsBookCategoryMongoDB() {
        HashMap<String, Object> response = new HashMap<>();
        StatisticsService statisticsService = new StatisticsService();
        HashMap<String, Object>[] stats = statisticsService.getAdminBookCategoryStatistics();

        response.put("stats", stats);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

    private Response adminStatisticsBookCategorySQL() {
        HashMap<String, Object> response = new HashMap<>();
        me.jack.lat.lmsbackendmongo.service.oracleDB.StatisticsService statisticsService = new me.jack.lat.lmsbackendmongo.service.oracleDB.StatisticsService();
        HashMap<String, Object>[] stats = statisticsService.getAdminBookCategoryStatistics();

        response.put("stats", stats);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

}
