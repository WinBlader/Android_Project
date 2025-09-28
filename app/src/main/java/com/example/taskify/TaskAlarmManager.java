package com.example.taskify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import android.util.Log;

public class TaskAlarmManager {
    
    private static final String DATE_TIME_FORMAT = "d/M/yyyy HH:mm";
    
    public static void scheduleTaskReminder(Context context, Task task) {
        scheduleTaskReminder(context, task, null);
    }
    
    public static void scheduleTaskReminder(Context context, Task task, String reminderTime) {
        try {
            // Parse the due date and time
            String dateTimeString = task.getDueDate() + " " + task.getDueTime();
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
            Date dueDate = sdf.parse(dateTimeString);
            
            if (dueDate == null) {
                return; // Invalid date format
            }
            
            Calendar reminderTimeCal = Calendar.getInstance();
            
            if (reminderTime != null && !reminderTime.isEmpty()) {
                // Use custom reminder time
                String reminderDateTimeString = task.getDueDate() + " " + reminderTime;
                Date reminderDate = sdf.parse(reminderDateTimeString);
                if (reminderDate != null) {
                    reminderTimeCal.setTime(reminderDate);
                } else {
                    // Fallback to 15 minutes before
                    reminderTimeCal.setTime(dueDate);
                    reminderTimeCal.add(Calendar.MINUTE, -15);
                }
            } else {
                // Default: 15 minutes before due time
                reminderTimeCal.setTime(dueDate);
                reminderTimeCal.add(Calendar.MINUTE, -15);
            }
            
            // Don't schedule if the reminder time has already passed
            if (reminderTimeCal.getTimeInMillis() <= System.currentTimeMillis()) {
                return;
            }
            
            // Create intent for alarm
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("taskName", task.getName());
            alarmIntent.putExtra("taskCategory", task.getCategory());
            alarmIntent.putExtra("taskPriority", task.getPriority());
            alarmIntent.putExtra("taskId", task.getId());
            
            // Create pending intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                (int) task.getId(), 
                alarmIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            // Schedule alarm
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeCal.getTimeInMillis(),
                    pendingIntent
                );
            }
            
        } catch (ParseException e) {
            Log.e("TaskAlarmManager", "Error parsing date/time for task reminder", e);
        }
    }
    
    public static void cancelTaskReminder(Context context, long taskId) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, 
            (int) taskId, 
            alarmIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
    
    public static void scheduleAllPendingTaskReminders(Context context) {
        try (TaskDBHelper dbHelper = new TaskDBHelper(context)) {
            List<Task> tasks = dbHelper.getAllTasks();
            for (Task task : tasks) {
                if ("Pending".equals(task.getStatus())) {
                    scheduleTaskReminder(context, task);
                }
            }
        }
    }
}
