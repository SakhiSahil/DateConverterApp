package com.devlyfe.dateconverter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import android.icu.util.IslamicCalendar;

public class HijriCalendar {
    private int year, month, day;

    public HijriCalendar() {
        fromGregorian(new GregorianCalendar());
    }

    public HijriCalendar(int year, int month, int day) {
        set(year, month, day);
    }

    public HijriCalendar(GregorianCalendar gc) {
        fromGregorian(gc);
    }

    /**
     * Converts the provided Gregorian calendar to a Hijri date.
     */
    public void fromGregorian(GregorianCalendar gc) {
        IslamicCalendar islamicCalendar = new IslamicCalendar(Locale.US);
        islamicCalendar.setTime(gc.getTime());
        // IslamicCalendar months are 0-indexed so we add 1.
        this.year = islamicCalendar.get(Calendar.YEAR);
        this.month = islamicCalendar.get(Calendar.MONTH) + 1;
        this.day = islamicCalendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Converts this Hijri date back to a Gregorian calendar.
     */
    public GregorianCalendar toGregorian() {
        IslamicCalendar islamicCalendar = new IslamicCalendar(Locale.US);
        // Set the IslamicCalendar fields. Note that we subtract 1 from the month.
        islamicCalendar.set(Calendar.YEAR, year);
        islamicCalendar.set(Calendar.MONTH, month - 1);
        islamicCalendar.set(Calendar.DAY_OF_MONTH, day);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(islamicCalendar.getTime());
        return gc;
    }

    /**
     * Returns the number of days in the current Hijri month.
     */
    public int getMonthLength() {
        IslamicCalendar islamicCalendar = new IslamicCalendar(Locale.US);
        islamicCalendar.set(Calendar.YEAR, this.year);
        islamicCalendar.set(Calendar.MONTH, this.month - 1);
        // Set to the first day of the month to obtain the proper maximum.
        islamicCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return islamicCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public void set(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
