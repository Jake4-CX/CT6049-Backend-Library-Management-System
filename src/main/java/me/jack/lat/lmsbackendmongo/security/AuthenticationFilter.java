package me.jack.lat.lmsbackendmongo.security;

import jakarta.annotation.Priority;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (resourceInfo.getResourceMethod().isAnnotationPresent(PermitAll.class)) {
            return;
        }

        if (resourceInfo.getResourceMethod().isAnnotationPresent(RolesAllowed.class)) {
            String role = resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class).value()[0];

            if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Missing or invalid token").build());
                return;
            }

            String authToken = authorizationHeader.substring("Bearer".length()).trim();
            String[] authTokenParts = authToken.split(":");
            // authToken = "secret:admin" where secret is the secret key and admin is the role

            if (authTokenParts.length != 2) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Missing or invalid token").build());
                return;
            }

            String secretKey = authTokenParts[0];

            if (!secretKey.equals("secret")) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid secret: " + secretKey).build());
                return;
            }

            String roleFromToken = authTokenParts[1];

            if (!roleFromToken.equals(role)) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid Role - given: " + roleFromToken).build());
                return;
            }

            requestContext.setProperty("role", roleFromToken);

        }
    }
}
