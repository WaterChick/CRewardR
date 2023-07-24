package cz.waterchick.creward.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TimeFormat {

    TIME,
    SECONDS,
    MINUTES,
    HOURS,
    DAYS;

    public static TimeFormat getTimeFormat(String timeFormatString){
        TimeFormat timeFormat = null;
        for(TimeFormat lTimeFormat : values()){
            Pattern pattern = Pattern.compile(lTimeFormat.toString(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(timeFormatString);
            boolean matchFound = matcher.find();
            if(matchFound){
                timeFormat = lTimeFormat;
                break;
            }
        }
        return timeFormat;
    }
}
