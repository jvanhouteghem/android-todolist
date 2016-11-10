package com.jonathan.vanhouteghem.todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vjona on 10/11/2016.
 */
public class DateHelper {

    private static String format = "HH:mm:ss";
    private static SimpleDateFormat formatter = null;

    /**
     * create formatted date from timestamp.
     *
     * @param timestamp
     * @return
     */
    public static String getFormattedDate(long timestamp) throws ParseException {
        if (formatter == null) {
            formatter = new SimpleDateFormat(format);
        }
        return formatter.format(new Date(timestamp)).toString();
    }
}