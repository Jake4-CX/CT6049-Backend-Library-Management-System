package me.jack.lat.lmsbackendmongo.util;

import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import io.github.cdimascio.dotenv.Dotenv;

public class MongoDBUtil {

    private static final Dotenv dotenv = Dotenv.configure().load();
    private static final String MONGODB_URI = dotenv.get("MONGODB_URI");

    private static final Datastore mongoDatastore;

    static {
        mongoDatastore = Morphia.createDatastore(
                MongoClients.create(MONGODB_URI),
                "lms"
        );

        mongoDatastore.getMapper().mapPackage(
                "me.jack.lat.lmsbackendmongo.entities"
        );

        mongoDatastore.ensureIndexes();
    }

    public static Datastore getMongoDatastore() {
        return mongoDatastore;
    }

}