package de.dmmm.lmapraktikum.utility;

import java.util.Calendar;

public class TimestampCreator {

    /**
     * Creates a timestamp in JavaScript format like "2019-05-24T18:49:30"
     * @return String
     */
    public static String createTimestampString(){

        //timezone = UTC
        //Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = (calendar.get(Calendar.MONTH)+1);
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String date = new String();

        date = "" + year;
        date += "-";

        if(month < 10) date += "0";
        date += month;
        date += "-";

        if(day < 10) date += "0";
        date += day;
        date += "T";

        if(hour < 10) date += "0";
        date += hour;
        date += ":";

        if(minute < 10) date += "0";
        date += minute;
        date += ":";

        if(second < 10) date += "0";
        date += second;

        return date;
    }
}
