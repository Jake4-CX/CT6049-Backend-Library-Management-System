package me.jack.lat.lmsbackendmongo.security;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import me.jack.lat.lmsbackendmongo.annotations.RestrictedRoles;
import me.jack.lat.lmsbackendmongo.annotations.UnprotectedRoute;
import me.jack.lat.lmsbackendmongo.entities.User;
import me.jack.lat.lmsbackendmongo.util.JwtUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (!(resourceInfo.getResourceMethod().isAnnotationPresent(RestrictedRoles.class)) || (resourceInfo.getResourceMethod().isAnnotationPresent(UnprotectedRoute.class))) {
            return;
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            abortWithUnauthorized(requestContext, "Missing or invalid token");
            return;
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();
        User.Role[] roles = resourceInfo.getResourceMethod().getAnnotation(RestrictedRoles.class).value();

        try {
            Claims claimsJws = JwtUtil.decodeAccessToken(token);
            String userRole = claimsJws.get("role", String.class);

            // Prevent users from accessing resources that they are not allowed to access.
            if (!Arrays.asList(roles).contains(User.Role.valueOf(userRole))) {
                abortWithUnauthorized(requestContext, "Invalid Role - given: " + userRole);
                return;
            }

            requestContext.setProperty("role", userRole);
            requestContext.setProperty("userId", claimsJws.getSubject());

        } catch (Exception e) {
            abortWithUnauthorized(requestContext, "Invalid token");
        }
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("error", new HashMap<String, Object>() {{
            put("message", message);
            put("type", 401);
        }});
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON).build());
    }
}
