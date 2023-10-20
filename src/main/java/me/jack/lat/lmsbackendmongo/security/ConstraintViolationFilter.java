package me.jack.lat.lmsbackendmongo.security;

import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class ConstraintViolationFilter implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            String[] pathParts = violation.getPropertyPath().toString().split("\\.");
            errors.put(pathParts[pathParts.length - 1], violation.getMessage());
        }

        Map<String, Object> error = new HashMap<>();
        error.put("errors", errors);
        error.put("type", 400);

        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("message", "Validation failed.");

        return Response.status(Response.Status.BAD_REQUEST).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
