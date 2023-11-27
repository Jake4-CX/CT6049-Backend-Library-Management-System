package me.jack.lat.lmsbackendmongo.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static Date convertStringToDate(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        try {
            return df.parse(date);
        } catch (ParseException e) {
            throw new ParseException("Failed to parse date: " + date, 0);
        }
    }

    public static String convertDateToStringFromWeb(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC")); // Set the timezone to UTC
        return df.format(date);
    }
}
