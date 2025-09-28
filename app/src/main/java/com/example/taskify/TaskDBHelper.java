package com.example.taskify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class TaskDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "taskDB";
    private static final int DB_VERSION = 1;

    private static final String TABLE_TASKS = "tasks";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_CATEGORY = "category";
    private static final String COL_PRIORITY = "priority";
    private static final String COL_DUEDATE = "dueDate";
    private static final String COL_DUETIME = "dueTime";
    private static final String COL_STATUS = "status";
    private static final String COL_SCORE = "score";

    public TaskDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT," +
                COL_CATEGORY + " TEXT," +
                COL_PRIORITY + " TEXT," +
                COL_DUEDATE + " TEXT," +
                COL_DUETIME + " TEXT," +
                COL_STATUS + " TEXT," +
                COL_SCORE + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public long addTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, task.getName());
        values.put(COL_CATEGORY, task.getCategory());
        values.put(COL_PRIORITY, task.getPriority());
        values.put(COL_DUEDATE, task.getDueDate());
        values.put(COL_DUETIME, task.getDueTime());
        values.put(COL_STATUS, task.getStatus());
        values.put(COL_SCORE, task.getScore());
        return db.insert(TABLE_TASKS, null, values);
    }

    public List<Task> getAllTasks(){
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Sort by priority: High (1), Medium (2), Low (3), then by due date
        String query = "SELECT * FROM " + TABLE_TASKS + 
                      " ORDER BY CASE " + COL_PRIORITY + 
                      " WHEN 'High' THEN 1 " +
                      " WHEN 'Medium' THEN 2 " +
                      " WHEN 'Low' THEN 3 " +
                      " ELSE 4 END, " + COL_DUEDATE + ", " + COL_DUETIME;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                Task task = new Task(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PRIORITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DUEDATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DUETIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCORE))
                );
                task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                taskList.add(task);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return taskList;
    }

    public int updateTaskStatusAndScore(long id, String status, int score){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, status);
        values.put(COL_SCORE, score);
        return db.update(TABLE_TASKS, values, COL_ID + "= ?", new String[]{String.valueOf(id)});
    }

    public int deleteTask(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TASKS, COL_ID + "= ?", new String[]{String.valueOf(id)});
    }

    public int markOverdueTasksAsFailed(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Get current date and time
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy HH:mm", java.util.Locale.getDefault());
        String currentDateTime = sdf.format(new java.util.Date());
        
        // Update pending tasks where due date + time is less than current date + time
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, "Failed");
        values.put(COL_SCORE, 0);
        
        // Use a simpler approach - check each task individually
        java.util.List<Task> pendingTasks = new java.util.ArrayList<>();
        Cursor cursor = db.query(TABLE_TASKS, null, COL_STATUS + " = ?", new String[]{"Pending"}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Task task = new Task(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PRIORITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DUEDATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DUETIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_SCORE))
                );
                task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                pendingTasks.add(task);
            }while(cursor.moveToNext());
        }
        cursor.close();
        
        int updatedCount = 0;
        for(Task task : pendingTasks){
            try{
                String taskDateTime = task.getDueDate() + " " + task.getDueTime();
                java.util.Date taskDate = sdf.parse(taskDateTime);
                java.util.Date currentDate = sdf.parse(currentDateTime);
                
                if(taskDate != null && currentDate != null && taskDate.before(currentDate)){
                    db.update(TABLE_TASKS, values, COL_ID + " = ?", new String[]{String.valueOf(task.getId())});
                    updatedCount++;
                }
            }catch(java.text.ParseException e){
                // Skip tasks with invalid date format
            }
        }
        
        return updatedCount;
    }
}


