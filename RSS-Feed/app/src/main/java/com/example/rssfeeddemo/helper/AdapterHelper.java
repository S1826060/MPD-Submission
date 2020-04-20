package com.example.rssfeeddemo.helper;

import org.threeten.bp.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

//S1826060 Scott Derek Robertson
public class AdapterHelper {

    public static int days(String startDate, String endDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;
        try {
            d = sdf.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int year1 = c.get(Calendar.YEAR);
        int month1 = c.get(Calendar.MONTH) + 1;
        int finalDate1 = c.get(Calendar.DATE);



        Date d1 = null;
        try {
            d1 = sdf.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);

        int year = c1.get(Calendar.YEAR);
        int month= c1.get(Calendar.MONTH) + 1;
        int finalDate = c1.get(Calendar.DATE);

        org.threeten.bp.LocalDate l1 = org.threeten.bp.LocalDate.of(year, month, finalDate);
        org.threeten.bp.LocalDate l2 = org.threeten.bp.LocalDate.of(year1, month1, finalDate1);

        org.threeten.bp.Period diff1 = Period.between(l2, l1);
        return diff1.getDays();
    }

    public static int getMonthNumber(String month){
        int num=0;
        switch (month){
            case "January":
                num = 1;
                break;
            case "February":
                num = 2;
                break;
            case "March":
                num = 3;
                break;
            case "April":
                num = 4;
                break;
            case "May":
                num = 5;
                break;
            case "June":
                num = 6;
                break;
            case "July":
                num = 7;
                break;
            case "August":
                num = 8;
                break;
            case "September":
                num = 9;
                break;
            case "October":
                num = 10;
                break;
            case "November":
                num = 11;
                break;
            case "December":
                num = 12;
                break;
        }
        return num;
    }
}
