package com.example.todolist;

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
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);
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
                taskList.add(task);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return taskList;
    }
}
