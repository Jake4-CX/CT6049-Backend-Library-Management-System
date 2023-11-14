package me.jack.lat.lmsbackendmongo.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static Date convertStringToDate(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        try {
            return df.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }
}
