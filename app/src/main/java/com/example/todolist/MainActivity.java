package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddTask;
    private TaskDBHelper dbHelper;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        fabAddTask = findViewById(R.id.fabAddTask);

        dbHelper = new TaskDBHelper(this);
        taskList = dbHelper.getAllTasks();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskList);
        recyclerView.setAdapter(taskAdapter);

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
    }
}
