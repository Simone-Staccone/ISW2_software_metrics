package utils;

import control.ConstantNames;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser {
    public static Date parseStringToDate(String date){
        try {
            return new SimpleDateFormat(ConstantNames.FORMATTING_STRING).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
