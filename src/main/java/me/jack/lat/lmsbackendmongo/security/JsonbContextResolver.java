package me.jack.lat.lmsbackendmongo.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.ext.Provider;
import jakarta.json.bind.Jsonb;
import jakarta.ws.rs.ext.ContextResolver;
import me.jack.lat.lmsbackendmongo.util.jsonb.Configurator;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JsonbContextResolver implements ContextResolver<Jsonb> {

    private final Jsonb jsonb = Configurator.createJsonbWithGlobalDateAdapter();

    @Override
    public Jsonb getContext(Class<?> type) {
        return jsonb;
    }
}