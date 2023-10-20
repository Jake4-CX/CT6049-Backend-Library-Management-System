package me.jack.lat.lmsbackendmongo.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.model.NewUser;
import me.jack.lat.lmsbackendmongo.util.MongoDBUtil;

import java.util.logging.Logger;

public class UserService {

    private final Datastore datastore;
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    public UserService() {
        this.datastore = MongoDBUtil.getMongoDatastore();
    }

    public boolean createUser(NewUser newUser) {

        if (userExists(newUser)) {
            return false;
        }

        if (newUser.getUserEmail() == null || newUser.getUserPassword() == null) {
            return false;
        }

        // Hash password with BCrypt
        String userPassword = BCrypt.withDefaults().hashToString(12, newUser.getUserPassword().toCharArray());

        User user = new User(newUser.getUserEmail(), userPassword);

        try {
            datastore.save(user);
        } catch (Exception e) {
            logger.warning("Failed saving user: " + e.getMessage());
            return false;
        }

        return true;

    }

    public boolean userExists(NewUser newUser) {
        long count = datastore.find(User.class)
                .filter(Filters.eq("userEmail", newUser.getUserEmail()))
                .count();
        return count > 0;
    }

    public boolean loginValidation(String userEmail, String userPassword) {
        User user = datastore.find(User.class)
                .filter(Filters.eq("userEmail", userEmail))
                .first();
        if (user == null) {
            return false;
        }
        return BCrypt.verifyer().verify(userPassword.toCharArray(), user.getUserPassword()).verified;
    }
}
