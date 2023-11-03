package me.jack.lat.lmsbackendmongo.util.jsonb;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

public class Configurator {

    public static Jsonb createJsonbWithGlobalDateAdapter() {
        JsonbConfig config = new JsonbConfig().withAdapters(new DateAdapter());

        return JsonbBuilder.create(config);
    }
}