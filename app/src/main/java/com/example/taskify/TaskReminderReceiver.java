package com.example.taskify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TaskReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "TaskReminderReceiver";
    private static final String CHANNEL_ID = "task_reminders";
    private static final int NOTIFICATION_ID = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received task reminder broadcast");
        
        long taskId = intent.getLongExtra("task_id", -1);
        String taskName = intent.getStringExtra("task_name");
        String taskCategory = intent.getStringExtra("task_category");
        String dueTime = intent.getStringExtra("due_time");

        if (taskId == -1 || taskName == null) {
            Log.e(TAG, "Invalid task data in intent");
            return;
        }

        // Check if task is still pending (not completed or failed)
        TaskDBHelper dbHelper = new TaskDBHelper(context);
        Task task = dbHelper.getTaskById(taskId);
        
        if (task == null || !task.getStatus().equals("Pending")) {
            Log.d(TAG, "Task no longer pending, skipping notification");
            return;
        }

        createNotificationChannel(context);
        showNotification(context, taskName, taskCategory, dueTime, taskId);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Reminders";
            String description = "Notifications for upcoming task deadlines";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(Context context, String taskName, String category, String dueTime, long taskId) {
        // Create intent to open the app when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            (int) taskId, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_taskify_logo)
                .setContentTitle("⏰ Task Reminder: " + taskName)
                .setContentText("Due in 5 minutes at " + dueTime)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Task: " + taskName + "\nCategory: " + category + "\nDue: " + dueTime + "\n\nDon't forget to complete this task!"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{0, 1000, 500, 1000});

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify((int) taskId, builder.build());
            Log.d(TAG, "Notification sent for task: " + taskName);
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to send notification: " + e.getMessage());
        }
    }
}
