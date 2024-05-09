package me.jack.lat.lmsbackendmongo.resources.statistics;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;
import java.util.HashMap;

@Path("/statistics/admin/overview")
public class AdminStatisticsOverviewResource {

    @GET
    @RestrictedRoles({User.Role.CHIEF_LIBRARIAN, User.Role.LIBRARIAN, User.Role.FINANCE_DIRECTOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Response adminStatisticsOverview() {
        return adminStatisticsOverviewSQL();
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
