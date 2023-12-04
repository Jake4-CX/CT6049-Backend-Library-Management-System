package me.jack.lat.lmsbackendmongo.service.mongoDB;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import me.jack.lat.lmsbackendmongo.entities.Book;
import me.jack.lat.lmsbackendmongo.entities.LoanedBook;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class StatisticsService {

    private final Datastore datastore;
    private static final Logger logger = Logger.getLogger(StatisticsService.class.getName());

    public StatisticsService() {
        this.datastore = MongoDBUtil.getMongoDatastore();
    }

    /**
        Admin Statistics Overview, returns totalBooks, totalUsers, totalIssuedBooks, totalOverdueBooks

        @return HashMap<String, Object>

     **/
    public HashMap<String, Object> getAdminStatisticsOverview() {

        HashMap<String, Object> stats = new HashMap<>();

        stats.put("totalBooks", datastore.getDatabase().getCollection("books").countDocuments());
        stats.put("totalUsers", datastore.getDatabase().getCollection("users").countDocuments());
        stats.put("totalIssuedBooks", datastore.find(LoanedBook.class).filter(
                Filters.eq("returnedAt", null)
        ).count());
        stats.put("totalOverdueBooks", datastore.find(LoanedBook.class).filter(
                Filters.and(
                        Filters.eq("returnedAt", null),
                        Filters.lte("loanedAt", System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000))
                )
        ).count());

        return stats;
    }

    /**
        Admin Book Circulation Statistics, returns totalIssuedBooks, totalOverdueBooks, totalNotOverdueBooks

        @return HashMap<String, Object>

     **/
    public HashMap<String, Object> getAdminBookCirculationStatistics() {

        HashMap<String, Object> stats = new HashMap<>();

        stats.put("totalIssuedBooks", datastore.find(LoanedBook.class).filter(
                Filters.eq("returnedAt", null)
        ).count());
        stats.put("totalOverdueBooks", datastore.find(LoanedBook.class).filter(
                Filters.and(
                        Filters.eq("returnedAt", null),
                        Filters.lte("loanedAt", System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000))
                )
        ).count());
        stats.put("totalNotOverdueBooks", datastore.find(LoanedBook.class).filter(
                Filters.and(
                        Filters.eq("returnedAt", null),
                        Filters.gt("loanedAt", System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000))
                )
        ).count());

        return stats;
    }

}