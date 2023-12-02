package me.jack.lat.lmsbackendmongo.resources.statistics;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.enums.DatabaseTypeEnum;
import me.jack.lat.lmsbackendmongo.service.mongoDB.StatisticsService;

import java.util.HashMap;

@Path("/statistics/admin/overview")
public class AdminStatisticsOverviewResource {

    @GET
    @RestrictedRoles({User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response adminStatisticsOverview(@HeaderParam("Database-Type") String databaseType) {

        if (databaseType == null || databaseType.isEmpty()) {
            databaseType = DatabaseTypeEnum.MONGODB.toString();
        }

        if (databaseType.equalsIgnoreCase(DatabaseTypeEnum.SQL.toString())) {
            return adminStatisticsOverviewSQL();
        } else {
            return adminStatisticsOverviewMongoDB();
        }
    }

    private Response adminStatisticsOverviewMongoDB() {
        HashMap<String, Object> response = new HashMap<>();

        StatisticsService statisticsService = new StatisticsService();
        HashMap<String, Object> stats = statisticsService.getAdminStatisticsOverview();

        if (stats != null) {
            response.put("stats", stats);
            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", "Failed to get admin statistics overview");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON).build();
        }
    }

    private Response adminStatisticsOverviewSQL() {
        HashMap<String, Object> response = new HashMap<>();

        me.jack.lat.lmsbackendmongo.service.oracleDB.StatisticsService statisticsService = new me.jack.lat.lmsbackendmongo.service.oracleDB.StatisticsService();
        HashMap<String, Object> stats = statisticsService.getAdminStatisticsOverview();

        if (stats != null) {
            response.put("stats", stats);
            return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
        } else {
            response.put("message", "Failed to get admin statistics overview");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON).build();
        }
    }
}
