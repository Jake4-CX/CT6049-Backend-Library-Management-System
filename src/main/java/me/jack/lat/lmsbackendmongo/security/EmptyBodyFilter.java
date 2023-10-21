package me.jack.lat.lmsbackendmongo.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class EmptyBodyFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (requestContext.getMethod().equals("POST") || requestContext.getMethod().equals("PUT")) {
            // Checking if the request has a body
            if (requestContext.getLength() <= 0 || requestContext.getEntityStream() == null || requestContext.getEntityStream().available() <= 0) {

                Map<String, Object> error = new HashMap<>();
                error.put("message", "Request body is missing.");
                error.put("type", 400);

                Map<String, Object> response = new HashMap<>();
                response.put("error", error);
                response.put("message", "Validation failed.");

                requestContext.abortWith(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity(response)
                                .type(MediaType.APPLICATION_JSON)
                                .build()
                );
            }
        }
    }
}
