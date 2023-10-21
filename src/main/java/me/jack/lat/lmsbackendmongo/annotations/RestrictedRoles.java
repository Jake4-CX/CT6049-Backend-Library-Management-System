package me.jack.lat.lmsbackendmongo.annotations;

import me.jack.lat.lmsbackendmongo.entities.User;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RestrictedRoles {
    User.Role value() default User.Role.USER;
}
