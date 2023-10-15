package me.jack.lat.lmsbackendmongo.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.cdimascio.dotenv.Dotenv;

public class MongoDBUtil {

    private static final Dotenv dotenv = Dotenv.configure().load();
    private static final String MONGODB_URI = dotenv.get("MONGODB_URI");
    private static MongoClient mongoClient;

    static {
        mongoClient = MongoClients.create(MONGODB_URI);
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }
}