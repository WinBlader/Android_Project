package com.example.taskify;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.appbar.MaterialToolbar;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddTask;
    private FloatingActionButton fabStats;
    private TaskDBHelper dbHelper;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private View contentContainer;
    private TextView tvCompletedCount, tvPendingCount, tvFailedCount;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize theme manager and apply saved theme
        themeManager = ThemeManager.getInstance(this);
        themeManager.applyTheme(this);
        
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        fabAddTask = findViewById(R.id.fabAddTask);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvFailedCount = findViewById(R.id.tvFailedCount);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_info) {
                    startActivity(new Intent(MainActivity.this, StatsActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.action_theme) {
                    toggleTheme();
                    return true;
                }
                return false;
            }
        });

        dbHelper = new TaskDBHelper(this);
        // Check for overdue tasks and mark them as failed
        dbHelper.markOverdueTasksAsFailed();
        taskList = dbHelper.getAllTasks();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskList);
        taskAdapter.setOnTaskDeleteListener(new TaskAdapter.OnTaskDeleteListener() {
            @Override
            public void onTaskDeleted(int position) {
                updateHeaderCounts();
            }
        });
        recyclerView.setAdapter(taskAdapter);
        updateHeaderCounts();

        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open AddTaskActivity
                startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
            }
        });

        
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check for overdue tasks and mark them as failed
        dbHelper.markOverdueTasksAsFailed();
        
        // Refresh task list after returning from AddTaskActivity
        taskList.clear();
        taskList.addAll(dbHelper.getAllTasks());
        taskAdapter.notifyDataSetChanged();
        updateHeaderCounts();
    }

    private void updateHeaderCounts(){
        int completed = 0, pending = 0, failed = 0;
        for(Task t : taskList){
            if ("Completed".equals(t.getStatus())) completed++;
            else if ("Failed".equals(t.getStatus())) failed++;
            else pending++;
        }
        if (tvCompletedCount != null) tvCompletedCount.setText("Completed: " + completed);
        if (tvPendingCount != null) tvPendingCount.setText("Pending: " + pending);
        if (tvFailedCount != null) tvFailedCount.setText("Failed: " + failed);
    }
    
    private void toggleTheme() {
        int currentMode = themeManager.getThemeMode();
        int newMode;
        String message;
        
        if (currentMode == ThemeManager.THEME_LIGHT) {
            newMode = ThemeManager.THEME_DARK;
            message = "Switched to Dark Mode";
        } else if (currentMode == ThemeManager.THEME_DARK) {
            newMode = ThemeManager.THEME_SYSTEM;
            message = "Switched to System Theme";
        } else {
            newMode = ThemeManager.THEME_LIGHT;
            message = "Switched to Light Mode";
        }
        
        themeManager.setThemeMode(newMode);
        recreate(); // Restart activity to apply new theme
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}


