package com.johngoodstadt.memorize.Libraries;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Extensions {


    public static Date dateFromString(SimpleDateFormat dateFormat, String strDate) {
        if (strDate.length() > 19)//e.g. "2016-02-29 12:24:26"
            return null;

        if (dateFormat.equals(""))
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(strDate);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static Date dateFromString(String strDate) {
        if (strDate.length() > 19)//e.g. "2016-02-29 12:24:26"
            return null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(strDate);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static int intValue(boolean boolValue) {
        if (boolValue)
            return 1;
        else
            return 0;

    }

    public static String dateForDB(Date date) {
        String format = "yyyy-MM-dd HH:mm:ss";
        return new SimpleDateFormat(format).format(date);
    }

    public static String stringFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateTim = "";
        try {
            dateTim = dateFormat.format(date);
            System.out.println("Current Date Time : " + dateTim);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTim;
    }

    public static String stringFromDate(Date date, SimpleDateFormat dateFormat) {
        String dateTim = "";
        try {
            dateTim = dateFormat.format(date);
            System.out.println("Current Date Time : " + dateTim);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTim;
    }
}