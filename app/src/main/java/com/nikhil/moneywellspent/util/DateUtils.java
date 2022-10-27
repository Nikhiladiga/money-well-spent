package com.nikhil.moneywellspent.util;

import android.annotation.SuppressLint;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DateUtils {

    public static DateUtils instance;

    public static synchronized DateUtils getInstance() {
        if (instance == null) {
            instance = new DateUtils();
        }
        return instance;
    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return new SimpleDateFormat("MMMM").format(calendar.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return Integer.parseInt(new SimpleDateFormat("yyyy").format(calendar.getTime()));
    }

    public String getDay(String date) {
        return LocalDate.parse(
                date,
                DateTimeFormatter.ofPattern("dd-MM-uuuu")
        ).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public ConcurrentHashMap<String, Long> getMonthStartAndMonthEndTimestamp(Integer currentYear, String currentMonth) {
        ConcurrentHashMap<String, Long> monthStartEndTs = new ConcurrentHashMap<>();
        Calendar calendar = Calendar.getInstance();
        Month month;
        if (currentMonth.equals("All")) {
            month = Month.valueOf("JANUARY");
        } else {
            month = Month.valueOf(currentMonth.toUpperCase(Locale.ROOT));
        }

        int monthIndex = month.getValue();
        int year;
        if (currentYear == null) {
            year = calendar.get(Calendar.YEAR);
        } else {
            year = currentYear;
        }
        int noOfDaysInMonth = month.length(checkIfLeapYear(year));
        int monthStartDay = Integer.parseInt(SharedPrefHelper.getMonthStartDay());

        LocalDate start = null;
        LocalDate end = null;

        //Check if startofMonthDate is more than number of days in current month
        if (monthStartDay > noOfDaysInMonth) {
            if (monthIndex == 11) { //Check if november
                //Start date
                start = LocalDate.of(year, monthIndex + 1, monthStartDay - noOfDaysInMonth);
                //End date
                end = LocalDate.of(year + 1, 1, monthStartDay - noOfDaysInMonth);
            } else if (monthIndex == 12) { //Check if december
                //Start date
                start = LocalDate.of(year + 1, 1, monthStartDay - noOfDaysInMonth);
                //End date
                end = LocalDate.of(year + 1, 2, monthStartDay - noOfDaysInMonth);
            } else { //Other months
                //Start date
                start = LocalDate.of(year, monthIndex + 1, monthStartDay - noOfDaysInMonth);
                //End date
                end = LocalDate.of(year, monthIndex + 2, monthStartDay - noOfDaysInMonth);
            }
        } else {

            if (currentMonth.equals("All")) {
                start = LocalDate.of(year, 1, monthStartDay);
                end = LocalDate.of(year + 1, 1, monthStartDay);
            } else if (monthIndex == 12 || monthIndex == 11) {
                start = LocalDate.of(year + 1, monthIndex, monthStartDay);
                end = LocalDate.of(year + 1, 1, monthStartDay);
            } else {
                start = LocalDate.of(year, monthIndex, monthStartDay);
                if (monthIndex == 1 && (Month.of(monthIndex + 2).length(checkIfLeapYear(year)) < monthStartDay)) { //January
                    if (checkIfLeapYear(year)) {
                        end = LocalDate.of(year, monthIndex + 2, monthStartDay - 29);
                    } else {
                        end = LocalDate.of(year, monthIndex + 2, monthStartDay - 28);
                    }
                } else if (Month.of(monthIndex + 1).length(checkIfLeapYear(year)) < monthStartDay) {
                    end = LocalDate.of(year, monthIndex + 2, monthStartDay - Month.of(monthIndex + 1).length(checkIfLeapYear(year)));
                } else {
                    end = LocalDate.of(year, monthIndex + 1, monthStartDay);
                }
            }
        }

        monthStartEndTs.put("start", start.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000);
        monthStartEndTs.put("end", end.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000);

        return monthStartEndTs;
    }

    public boolean checkIfLeapYear(int year) {
        return (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
    }

    public Timestamp convertStringToTimestamp(String strDate) {
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            // you can change format of date
            Date date = formatter.parse(strDate);
            return new Timestamp(Objects.requireNonNull(date).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public String convertTimestampToDate(Long timestamp) {
        if (timestamp != null) {
            return new SimpleDateFormat("dd-MM-yyyy").format(new Date(timestamp));
        } else {
            return "";
        }
    }

}
