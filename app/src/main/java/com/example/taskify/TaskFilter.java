package com.example.taskify;

import android.content.Intent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Helper class that encapsulates task filtering logic based on due dates.
 */
public class TaskFilter {

    public static final String EXTRA_TYPE = "extra_filter_type";
    public static final String EXTRA_DATE_MILLIS = "extra_filter_date_millis";
    public static final String EXTRA_MONTH = "extra_filter_month";
    public static final String EXTRA_YEAR = "extra_filter_year";

    public enum Type {
        ALL,
        DATE,
        MONTH,
        YEAR
    }

    private static final String INPUT_DATE_PATTERN = "d/M/yyyy";
    private static final SimpleDateFormat INPUT_FORMAT =
            new SimpleDateFormat(INPUT_DATE_PATTERN, Locale.getDefault());

    private Type type = Type.ALL;
    private final Calendar calendar = Calendar.getInstance();
    private int month = -1; // 0-11
    private int year = -1;

    public Type getType() {
        return type;
    }

    public void setAll() {
        type = Type.ALL;
        month = -1;
        year = -1;
    }

    public void setDate(int year, int month, int dayOfMonth) {
        type = Type.DATE;
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        this.year = year;
        this.month = month;
    }

    public void setMonth(int year, int month) {
        type = Type.MONTH;
        this.year = year;
        this.month = month;
    }

    public void setYear(int year) {
        type = Type.YEAR;
        this.year = year;
        this.month = -1;
    }

    public long getSelectedDateMillis() {
        if (type != Type.DATE) {
            return -1;
        }
        return calendar.getTimeInMillis();
    }

    public int getSelectedMonth() {
        return month;
    }

    public int getSelectedYear() {
        return year;
    }

    public void applyFromIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        String typeValue = intent.getStringExtra(EXTRA_TYPE);
        if (typeValue == null) {
            return;
        }
        Type intentType = Type.valueOf(typeValue);
        switch (intentType) {
            case DATE:
                long millis = intent.getLongExtra(EXTRA_DATE_MILLIS, -1);
                if (millis > 0) {
                    calendar.setTimeInMillis(millis);
                    setDate(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                }
                break;
            case MONTH:
                int intentMonth = intent.getIntExtra(EXTRA_MONTH, -1);
                int intentYear = intent.getIntExtra(EXTRA_YEAR, -1);
                if (intentMonth >= 0 && intentYear > 0) {
                    setMonth(intentYear, intentMonth);
                }
                break;
            case YEAR:
                int yearValue = intent.getIntExtra(EXTRA_YEAR, -1);
                if (yearValue > 0) {
                    setYear(yearValue);
                }
                break;
            case ALL:
            default:
                setAll();
                break;
        }
    }

    public void writeToIntent(Intent intent) {
        intent.putExtra(EXTRA_TYPE, type.name());
        switch (type) {
            case DATE:
                intent.putExtra(EXTRA_DATE_MILLIS, getSelectedDateMillis());
                break;
            case MONTH:
                intent.putExtra(EXTRA_MONTH, month);
                intent.putExtra(EXTRA_YEAR, year);
                break;
            case YEAR:
                intent.putExtra(EXTRA_YEAR, year);
                break;
            case ALL:
            default:
                break;
        }
    }

    public List<Task> apply(List<Task> source) {
        if (source == null || source.isEmpty()) {
            return source;
        }
        if (type == Type.ALL) {
            return new ArrayList<>(source);
        }
        List<Task> filtered = new ArrayList<>();
        for (Task task : source) {
            String dueDate = task.getDueDate();
            if (dueDate == null) {
                continue;
            }
            Calendar taskCal = parseDate(dueDate);
            if (taskCal == null) {
                continue;
            }
            if (matches(taskCal)) {
                filtered.add(task);
            }
        }
        return filtered;
    }

    private Calendar parseDate(String dateString) {
        synchronized (INPUT_FORMAT) {
            try {
                Date date = INPUT_FORMAT.parse(dateString);
                if (date == null) {
                    return null;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                zeroTime(cal);
                return cal;
            } catch (ParseException e) {
                return null;
            }
        }
    }

    private boolean matches(Calendar taskCal) {
        switch (type) {
            case DATE:
                return taskCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                        && taskCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                        && taskCal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH);
            case MONTH:
                return taskCal.get(Calendar.YEAR) == year
                        && taskCal.get(Calendar.MONTH) == month;
            case YEAR:
                return taskCal.get(Calendar.YEAR) == year;
            case ALL:
            default:
                return true;
        }
    }

    public String getDisplayLabel(String defaultLabel) {
        switch (type) {
            case DATE:
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
                return dateFormat.format(calendar.getTime());
            case MONTH:
                SimpleDateFormat monthFormat =
                        new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.YEAR, year);
                temp.set(Calendar.MONTH, month);
                temp.set(Calendar.DAY_OF_MONTH, 1);
                return monthFormat.format(temp.getTime());
            case YEAR:
                return String.valueOf(year);
            case ALL:
            default:
                if (defaultLabel != null) {
                    return defaultLabel;
                }
                return "All tasks";
        }
    }

    private void zeroTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}


