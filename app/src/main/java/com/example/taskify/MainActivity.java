package com.example.taskify;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                }
                return false;
            }
        });

        dbHelper = new TaskDBHelper(this);
        taskList = dbHelper.getAllTasks();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskList);
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
}


