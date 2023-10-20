package me.jack.lat.lmsbackendmongo.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class ProcessingExceptionFilter implements ExceptionMapper<ProcessingException> {

    @Override
    public Response toResponse(ProcessingException exception) {

        Map<String, Object> error = new HashMap<>();
        error.put("type", 400);
        error.put("message", "Malformed JSON payload provided.");

        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("message", "Validation failed.");

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(response)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}