package me.jack.lat.lmsbackendmongo.util.jsonb;

import jakarta.json.bind.adapter.JsonbAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAdapter implements JsonbAdapter<Date, String> {

    private static final ThreadLocal<SimpleDateFormat> dateFormat =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));

    @Override
    public String adaptToJson(Date date) {
        return dateFormat.get().format(date);
    }

    @Override
    public Date adaptFromJson(String dateStr) throws Exception {
        return dateFormat.get().parse(dateStr);
    }
}