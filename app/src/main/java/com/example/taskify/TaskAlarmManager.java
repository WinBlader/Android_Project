package com.example.taskify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskAlarmManager {
    private static final String TAG = "TaskAlarmManager";
    private Context context;
    private AlarmManager alarmManager;

    public TaskAlarmManager(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleTaskReminder(Task task) {
        try {
            // Parse due date and time
            String dateTimeString = task.getDueDate() + " " + task.getDueTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date dueDate = sdf.parse(dateTimeString);
            
            if (dueDate == null) {
                Log.e(TAG, "Failed to parse date: " + dateTimeString);
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dueDate);
            
            // Schedule alarm 5 minutes before due time
            calendar.add(Calendar.MINUTE, -5);
            
            // Don't schedule if the reminder time has already passed
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                Log.d(TAG, "Reminder time has already passed for task: " + task.getName());
                return;
            }

            Intent intent = new Intent(context, TaskReminderReceiver.class);
            intent.putExtra("task_id", task.getId());
            intent.putExtra("task_name", task.getName());
            intent.putExtra("task_category", task.getCategory());
            intent.putExtra("due_time", task.getDueTime());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 
                (int) task.getId(), 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
                );
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
                );
            }

            Log.d(TAG, "Scheduled reminder for task: " + task.getName() + " at " + calendar.getTime());

        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date for task: " + task.getName(), e);
        }
    }

    public void cancelTaskReminder(long taskId) {
        Intent intent = new Intent(context, TaskReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            (int) taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "Cancelled reminder for task ID: " + taskId);
    }

    public void rescheduleAllReminders() {
        TaskDBHelper dbHelper = new TaskDBHelper(context);
        java.util.List<Task> allTasks = dbHelper.getAllTasks();
        
        for (Task task : allTasks) {
            if (!task.getStatus().equals("Completed") && !task.getStatus().equals("Failed")) {
                scheduleTaskReminder(task);
            }
        }
    }
}
