package com.devlyfe.dateconverter;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.shawnlin.numberpicker.NumberPicker;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    // Calendar picker declarations
    private NumberPicker grYear, grMonth, grDay;     // Gregorian
    private NumberPicker shYear, shMonth, shDay;     // Jalali (Shamsi)
    private NumberPicker hjYear, hjMonth, hjDay;     // Hijri

    // TextViews for weekday display.
    private TextView grweekday, weekday;

    // Suppression and update flags to avoid recursive triggers.
    private boolean suppressJalali = false;
    private boolean suppressGregorian = false;
    private boolean suppressHijri = false;
    private boolean isUpdatingFromJalali = false;
    private boolean isUpdatingFromGregorian = false;
    private boolean isUpdatingFromHijri = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Gregorian NumberPickers.
        grYear = findViewById(R.id.gr_year);
        grMonth = findViewById(R.id.gr_month);
        grDay = findViewById(R.id.gr_day);

        // Initialize Jalali (Shamsi) NumberPickers.
        shYear = findViewById(R.id.sh_year);
        shMonth = findViewById(R.id.sh_month);
        shDay = findViewById(R.id.sh_day);

        // Initialize Islamic Hijri NumberPickers.
        hjYear = findViewById(R.id.hj_year);
        hjMonth = findViewById(R.id.hj_month);
        hjDay = findViewById(R.id.hj_day);

        // Initialize TextViews for weekdays.
        grweekday = findViewById(R.id.grweekday);
        weekday = findViewById(R.id.weekday);

        // Enable wrapping for all pickers.
        grYear.setWrapSelectorWheel(true);
        grMonth.setWrapSelectorWheel(true);
        grDay.setWrapSelectorWheel(true);
        shYear.setWrapSelectorWheel(true);
        shMonth.setWrapSelectorWheel(true);
        shDay.setWrapSelectorWheel(true);
        hjYear.setWrapSelectorWheel(true);
        hjMonth.setWrapSelectorWheel(true);
        hjDay.setWrapSelectorWheel(true);

        // -------------------------------
        // Listeners for Jalali (Shamsi) Pickers
        // -------------------------------
        shDay.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (suppressJalali) return;
            adjustJalaliDay(oldVal, newVal);
            updateFromJalali();
        });

        shMonth.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (suppressJalali) return;
            adjustJalaliMonth(oldVal, newVal);
            int newMax = new JalaliCalendar(shYear.getValue(), shMonth.getValue(), shDay.getValue()).getMonthLength();
            shDay.setMaxValue(newMax);
            if (shDay.getValue() > newMax) {
                shDay.setValue(newMax);
            }
            updateFromJalali();
        });

        shYear.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (suppressJalali) return;
            // Wrap-around handling for year (if needed).
            if (oldVal == shYear.getMaxValue() && newVal == shYear.getMinValue()) {
                suppressJalali = true;
                shYear.setValue(shYear.getMinValue());
                suppressJalali = false;
            } else if (oldVal == shYear.getMinValue() && newVal == shYear.getMaxValue()) {
                suppressJalali = true;
                shYear.setValue(shYear.getMaxValue());
                suppressJalali = false;
            }
            int newMax = new JalaliCalendar(shYear.getValue(), shMonth.getValue(), shDay.getValue()).getMonthLength();
            shDay.setMaxValue(newMax);
            if (shDay.getValue() > newMax) {
                shDay.setValue(newMax);
            }
            updateFromJalali();
        });

        // -------------------------------
        // Listeners for Gregorian Pickers
        // -------------------------------
        grDay.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (suppressGregorian) return;
            adjustGregorianDay(oldVal, newVal);
            updateFromGregorian();
        });

        grMonth.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (suppressGregorian) return;
            adjustGregorianMonth(oldVal, newVal);
            int maxDay = getGregorianMonthLength(grYear.getValue(), grMonth.getValue());
            grDay.setMaxValue(maxDay);
            if (grDay.getValue() > maxDay) {
                grDay.setValue(maxDay);
            }
            updateFromGregorian();
        });

        grYear.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (suppressGregorian) return;
            if (oldVal == grYear.getMaxValue() && newVal == grYear.getMinValue()) {
                suppressGregorian = true;
                grYear.setValue(grYear.getMinValue());
                suppressGregorian = false;
            } else if (oldVal == grYear.getMinValue() && newVal == grYear.getMaxValue()) {
                suppressGregorian = true;
                grYear.setValue(grYear.getMaxValue());
                suppressGregorian = false;
            }
            int maxDay = getGregorianMonthLength(grYear.getValue(), grMonth.getValue());
            grDay.setMaxValue(maxDay);
            if (grDay.getValue() > maxDay) {
                grDay.setValue(maxDay);
            }
            updateFromGregorian();
        });

        // -------------------------------
        // Listeners for Hijri Pickers
        // -------------------------------
        hjDay.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (suppressHijri) return;
            adjustHijriDay(oldVal, newVal);
            updateFromHijri();
        });
        hjMonth.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (suppressHijri) return;
            adjustHijriMonth(oldVal, newVal);
            int newMax = new HijriCalendar(hjYear.getValue(), hjMonth.getValue(), hjDay.getValue()).getMonthLength();
            hjDay.setMaxValue(newMax);
            if (hjDay.getValue() > newMax) {
                hjDay.setValue(newMax);
            }
            updateFromHijri();
        });
        hjYear.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (suppressHijri) return;
            // Optional wrap-around for Hijri year if desired.
            if (oldVal == hjYear.getMaxValue() && newVal == hjYear.getMinValue()) {
                suppressHijri = true;
                hjYear.setValue(hjYear.getMinValue());
                suppressHijri = false;
            } else if (oldVal == hjYear.getMinValue() && newVal == hjYear.getMaxValue()) {
                suppressHijri = true;
                hjYear.setValue(hjYear.getMaxValue());
                suppressHijri = false;
            }
            int newMax = new HijriCalendar(hjYear.getValue(), hjMonth.getValue(), hjDay.getValue()).getMonthLength();
            hjDay.setMaxValue(newMax);
            if (hjDay.getValue() > newMax) {
                hjDay.setValue(newMax);
            }
            updateFromHijri();
        });

        // -------------------------------
        // Initialize using the Current System Date (Gregorian as default)
        // -------------------------------
        GregorianCalendar systemDate = new GregorianCalendar();
        grYear.setValue(systemDate.get(Calendar.YEAR));
        grMonth.setValue(systemDate.get(Calendar.MONTH) + 1); // Calendar.MONTH is zero-indexed.
        grDay.setValue(systemDate.get(Calendar.DAY_OF_MONTH));
        updateFromGregorian();
    }

    // -------------------------------
    // Helper Methods for Gregorian Month Length
    // -------------------------------
    private boolean isGregorianLeap(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private int getGregorianMonthLength(int year, int month) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return isGregorianLeap(year) ? 29 : 28;
            default:
                return 0;
        }
    }

    // -------------------------------
    // Wrap-Around Adjustments for Jalali Pickers
    // -------------------------------
    private void adjustJalaliDay(int oldVal, int newVal) {
        int currentMax = new JalaliCalendar(shYear.getValue(), shMonth.getValue(), shDay.getValue()).getMonthLength();

        if (oldVal == currentMax && newVal == shDay.getMinValue()) {
            suppressJalali = true;
            int currentMonth = shMonth.getValue();
            if (currentMonth == shMonth.getMaxValue()) {
                shMonth.setValue(shMonth.getMinValue());
                shYear.setValue(shYear.getValue() + 1);
            } else {
                shMonth.setValue(currentMonth + 1);
            }
            shDay.setValue(shDay.getMinValue());
            int newMax = new JalaliCalendar(shYear.getValue(), shMonth.getValue(), shDay.getValue()).getMonthLength();
            shDay.setMaxValue(newMax);
            suppressJalali = false;
        } else if (oldVal == shDay.getMinValue() && newVal == currentMax) {
            suppressJalali = true;
            int currentMonth = shMonth.getValue();
            if (currentMonth == shMonth.getMinValue()) {
                shMonth.setValue(shMonth.getMaxValue());
                shYear.setValue(shYear.getValue() - 1);
            } else {
                shMonth.setValue(currentMonth - 1);
            }
            int newMax = new JalaliCalendar(shYear.getValue(), shMonth.getValue(), 1).getMonthLength();
            shDay.setMaxValue(newMax);
            shDay.setValue(newMax);
            suppressJalali = false;
        }
    }

    private void adjustJalaliMonth(int oldVal, int newVal) {
        if (oldVal == shMonth.getMaxValue() && newVal == shMonth.getMinValue()) {
            suppressJalali = true;
            shYear.setValue(shYear.getValue() + 1);
            suppressJalali = false;
        } else if (oldVal == shMonth.getMinValue() && newVal == shMonth.getMaxValue()) {
            suppressJalali = true;
            shYear.setValue(shYear.getValue() - 1);
            suppressJalali = false;
        }
    }

    // -------------------------------
    // Wrap-Around Adjustments for Gregorian Pickers
    // -------------------------------
    private void adjustGregorianDay(int oldVal, int newVal) {
        int currentMax = getGregorianMonthLength(grYear.getValue(), grMonth.getValue());
        if (oldVal == currentMax && newVal == grDay.getMinValue()) {
            suppressGregorian = true;
            int currentMonth = grMonth.getValue();
            if (currentMonth == grMonth.getMaxValue()) {
                grMonth.setValue(grMonth.getMinValue());
                grYear.setValue(grYear.getValue() + 1);
            } else {
                grMonth.setValue(currentMonth + 1);
            }
            grDay.setValue(grDay.getMinValue());
            int newMax = getGregorianMonthLength(grYear.getValue(), grMonth.getValue());
            grDay.setMaxValue(newMax);
            suppressGregorian = false;
        } else if (oldVal == grDay.getMinValue() && newVal == currentMax) {
            suppressGregorian = true;
            int currentMonth = grMonth.getValue();
            if (currentMonth == grMonth.getMinValue()) {
                grMonth.setValue(grMonth.getMaxValue());
                grYear.setValue(grYear.getValue() - 1);
            } else {
                grMonth.setValue(currentMonth - 1);
            }
            int newMax = getGregorianMonthLength(grYear.getValue(), grMonth.getValue());
            grDay.setMaxValue(newMax);
            grDay.setValue(newMax);
            suppressGregorian = false;
        }
    }

    private void adjustGregorianMonth(int oldVal, int newVal) {
        if (oldVal == grMonth.getMaxValue() && newVal == grMonth.getMinValue()) {
            suppressGregorian = true;
            grYear.setValue(grYear.getValue() + 1);
            suppressGregorian = false;
        } else if (oldVal == grMonth.getMinValue() && newVal == grMonth.getMaxValue()) {
            suppressGregorian = true;
            grYear.setValue(grYear.getValue() - 1);
            suppressGregorian = false;
        }
    }

    // -------------------------------
    // Wrap-Around Adjustments for Hijri Pickers
    // -------------------------------
    private void adjustHijriDay(int oldVal, int newVal) {
        int currentMax = new HijriCalendar(hjYear.getValue(), hjMonth.getValue(), hjDay.getValue()).getMonthLength();
        if (oldVal == currentMax && newVal == hjDay.getMinValue()) {
            suppressHijri = true;
            int currentMonth = hjMonth.getValue();
            if (currentMonth == hjMonth.getMaxValue()) {
                hjMonth.setValue(hjMonth.getMinValue());
                hjYear.setValue(hjYear.getValue() + 1);
            } else {
                hjMonth.setValue(currentMonth + 1);
            }
            hjDay.setValue(hjDay.getMinValue());
            int newMax = new HijriCalendar(hjYear.getValue(), hjMonth.getValue(), hjDay.getValue()).getMonthLength();
            hjDay.setMaxValue(newMax);
            suppressHijri = false;
        } else if (oldVal == hjDay.getMinValue() && newVal == currentMax) {
            suppressHijri = true;
            int currentMonth = hjMonth.getValue();
            if (currentMonth == hjMonth.getMinValue()) {
                hjMonth.setValue(hjMonth.getMaxValue());
                hjYear.setValue(hjYear.getValue() - 1);
            } else {
                hjMonth.setValue(currentMonth - 1);
            }
            int newMax = new HijriCalendar(hjYear.getValue(), hjMonth.getValue(), 1).getMonthLength();
            hjDay.setMaxValue(newMax);
            hjDay.setValue(newMax);
            suppressHijri = false;
        }
    }

    private void adjustHijriMonth(int oldVal, int newVal) {
        if (oldVal == hjMonth.getMaxValue() && newVal == hjMonth.getMinValue()) {
            suppressHijri = true;
            hjYear.setValue(hjYear.getValue() + 1);
            suppressHijri = false;
        } else if (oldVal == hjMonth.getMinValue() && newVal == hjMonth.getMaxValue()) {
            suppressHijri = true;
            hjYear.setValue(hjYear.getValue() - 1);
            suppressHijri = false;
        }
    }

    // -------------------------------
    // Conversion Methods Between Calendars
    // -------------------------------

    /**
     * Converts the current Gregorian date (from its pickers) to
     * update both Jalali and Hijri calendars.
     */
    private void updateFromGregorian() {
        if (isUpdatingFromGregorian) return;
        isUpdatingFromGregorian = true;

        int year = grYear.getValue();
        int month = grMonth.getValue();
        int day = grDay.getValue();
        GregorianCalendar gc = new GregorianCalendar(year, month - 1, day);

        // --- Update Jalali ---
        suppressJalali = true;
        JalaliCalendar jCal = new JalaliCalendar(gc);
        shYear.setValue(jCal.getYear());
        shMonth.setValue(jCal.getMonth());
        int maxDayJalali = jCal.getMonthLength();
        shDay.setMaxValue(maxDayJalali);
        shDay.setValue(jCal.getDay() > maxDayJalali ? maxDayJalali : jCal.getDay());
        suppressJalali = false;

        // --- Update Hijri ---
        suppressHijri = true;
        HijriCalendar hCal = new HijriCalendar(gc);
        hjYear.setValue(hCal.getYear());
        hjMonth.setValue(hCal.getMonth());
        int maxDayHijri = hCal.getMonthLength();
        hjDay.setMaxValue(maxDayHijri);
        hjDay.setValue(hCal.getDay() > maxDayHijri ? maxDayHijri : hCal.getDay());
        suppressHijri = false;

        updateWeekdays(gc);
        isUpdatingFromGregorian = false;
    }

    /**
     * Converts the current Jalali (Shamsi) date (from its pickers)
     * to Gregorian (pivot) and then uses that pivot to update both Gregorian and Hijri.
     */
    private void updateFromJalali() {
        if (isUpdatingFromJalali) return;
        isUpdatingFromJalali = true;

        int year = shYear.getValue();
        int month = shMonth.getValue();
        int day = shDay.getValue();
        JalaliCalendar jCal = new JalaliCalendar(year, month, day);
        GregorianCalendar gc = jCal.toGregorian();

        // --- Update Gregorian ---
        suppressGregorian = true;
        grYear.setValue(gc.get(Calendar.YEAR));
        grMonth.setValue(gc.get(Calendar.MONTH) + 1);
        int maxDayGregorian = getGregorianMonthLength(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH) + 1);
        grDay.setMaxValue(maxDayGregorian);
        grDay.setValue(gc.get(Calendar.DAY_OF_MONTH));
        updateWeekdays(gc);
        suppressGregorian = false;

        // --- Update Hijri ---
        suppressHijri = true;
        HijriCalendar hCal = new HijriCalendar(gc);
        hjYear.setValue(hCal.getYear());
        hjMonth.setValue(hCal.getMonth());
        int maxDayHijri = hCal.getMonthLength();
        hjDay.setMaxValue(maxDayHijri);
        hjDay.setValue(hCal.getDay() > maxDayHijri ? maxDayHijri : hCal.getDay());
        suppressHijri = false;

        isUpdatingFromJalali = false;
    }

    /**
     * Converts the current Hijri date (from its pickers)
     * to Gregorian (pivot) and then updates both Gregorian and Jalali.
     */
    private void updateFromHijri() {
        if (isUpdatingFromHijri) return;
        isUpdatingFromHijri = true;

        int year = hjYear.getValue();
        int month = hjMonth.getValue();
        int day = hjDay.getValue();
        HijriCalendar hCal = new HijriCalendar(year, month, day);
        GregorianCalendar gc = hCal.toGregorian();

        // --- Update Gregorian ---
        suppressGregorian = true;
        grYear.setValue(gc.get(Calendar.YEAR));
        grMonth.setValue(gc.get(Calendar.MONTH) + 1);
        int maxDayGregorian = getGregorianMonthLength(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH) + 1);
        grDay.setMaxValue(maxDayGregorian);
        grDay.setValue(gc.get(Calendar.DAY_OF_MONTH));
        updateWeekdays(gc);
        suppressGregorian = false;

        // --- Update Jalali ---
        suppressJalali = true;
        JalaliCalendar jCal = new JalaliCalendar(gc);
        shYear.setValue(jCal.getYear());
        shMonth.setValue(jCal.getMonth());
        int maxDayJalali = jCal.getMonthLength();
        shDay.setMaxValue(maxDayJalali);
        shDay.setValue(jCal.getDay() > maxDayJalali ? maxDayJalali : jCal.getDay());
        suppressJalali = false;

        isUpdatingFromHijri = false;
    }

    // -------------------------------
    // Weekday Helper Methods
    // -------------------------------
    private void updateWeekdays(GregorianCalendar gc) {


        int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK);
        String englishDay = getEnglishDayName(dayOfWeek);
        grweekday.setText(englishDay);
        String persianDay = convertEnglishToPersian(englishDay);
        if ("جمعه".equals(persianDay)) {
            grweekday.setTextColor(Color.rgb(251, 177, 23));
            weekday.setTextColor(Color.rgb(251, 177, 23));
        } else {
            // Get color from current theme (textColorPrimary)
            TypedValue typedValue = new TypedValue();
            Context context = grweekday.getContext();
            Resources.Theme theme = context.getTheme();

            // Resolve the attribute for textColorPrimary
            theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);

            // Get the actual color from the resolved resource
            int color = ContextCompat.getColor(context, typedValue.resourceId);

            grweekday.setTextColor(color);
            weekday.setTextColor(color);
        }
        weekday.setText(persianDay);
    }

    private String getEnglishDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:    return "Sunday";
            case Calendar.MONDAY:    return "Monday";
            case Calendar.TUESDAY:   return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY:  return "Thursday";
            case Calendar.FRIDAY:    return "Friday";
            case Calendar.SATURDAY:  return "Saturday";
            default:                 return "";
        }
    }

    private String convertEnglishToPersian(String englishDay) {
        switch (englishDay) {
            case "Sunday":    return "یکشنبه";
            case "Monday":    return "دوشنبه";
            case "Tuesday":   return "سه‌شنبه";
            case "Wednesday": return "چهارشنبه";
            case "Thursday":  return "پنجشنبه";
            case "Friday":    return "جمعه";
            case "Saturday":  return "شنبه";
            default:          return "نامعلوم";
        }
    }
}
