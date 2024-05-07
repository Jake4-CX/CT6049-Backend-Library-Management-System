package me.jack.lat.lmsbackendmongo.resources.statistics;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.entities.User;

import java.util.HashMap;

@Path("/statistics/admin/book-category")
public class AdminStatisticsBookCategoryResource {

    @GET
    @RestrictedRoles({User.Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response adminStatisticsBookCategory() {
        return adminStatisticsBookCategorySQL();
    }

    private Response adminStatisticsBookCategorySQL() {
        HashMap<String, Object> response = new HashMap<>();
        me.jack.lat.lmsbackendmongo.service.oracleDB.StatisticsService statisticsService = new me.jack.lat.lmsbackendmongo.service.oracleDB.StatisticsService();
        HashMap<String, Object>[] stats = statisticsService.getAdminBookCategoryStatistics();

        response.put("stats", stats);

        return Response.status(Response.Status.OK).entity(response).type(MediaType.APPLICATION_JSON).build();
    }

}
