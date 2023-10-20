package me.jack.lat.lmsbackendmongo.security;

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

import java.io.IOException;
import java.util.HashMap;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (resourceInfo.getResourceMethod().isAnnotationPresent(UnprotectedRoute.class)) {
            return;
        }

        if (resourceInfo.getResourceMethod().isAnnotationPresent(RestrictedRoles.class)) {
            HashMap<String, Object> response = new HashMap<>();
            String role = resourceInfo.getResourceMethod().getAnnotation(RestrictedRoles.class).value()[0];

            if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
                response.put("error", new HashMap<String, Object>() {{
                    put("message", "Missing or invalid token");
                    put("type", 401);
                }});

                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON).build());
                return;
            }

            String authToken = authorizationHeader.substring("Bearer".length()).trim();
            String[] authTokenParts = authToken.split(":");
            // authToken = "secret:admin" where secret is the secret key and admin is the role

            if (authTokenParts.length != 2) {

                response.put("error", new HashMap<String, Object>() {{
                    put("message", "Missing or invalid token");
                    put("type", 401);
                }});

                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(response).type(MediaType.APPLICATION_JSON).build());
                return;
            }

            String secretKey = authTokenParts[0];

            if (!secretKey.equals("secret")) {

                response.put("error", new HashMap<String, Object>() {{
                    put("message", "Invalid authorization secret");
                    put("type", 401);
                }});

                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid secret: " + secretKey).type(MediaType.APPLICATION_JSON).build());
                return;
            }

            String roleFromToken = authTokenParts[1];

            if (!roleFromToken.equals(role)) {

                response.put("error", new HashMap<String, Object>() {{
                    put("message", "Invalid authorization secret");
                    put("type", 401);
                }});

                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid Role - given: " + roleFromToken).type(MediaType.APPLICATION_JSON).build());
                return;
            }

            requestContext.setProperty("role", roleFromToken);

        }
    }
}
