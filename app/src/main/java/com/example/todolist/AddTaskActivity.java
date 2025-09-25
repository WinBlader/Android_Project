package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTaskName, etTaskCategory, etDueDate, etDueTime;
    private Spinner spinnerPriority;
    private Button btnSaveTask;
    private TaskDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTaskName = findViewById(R.id.etTaskName);
        etTaskCategory = findViewById(R.id.etTaskCategory);
        etDueDate = findViewById(R.id.etDueDate);
        etDueTime = findViewById(R.id.etDueTime);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        btnSaveTask = findViewById(R.id.btnSaveTask);

        dbHelper = new TaskDBHelper(this);

        // Set up priority spinner
        String[] priorities = {"Low", "Medium", "High"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        // Due date picker
        etDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(AddTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etDueDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        // Due time picker
        etDueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        etDueTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true);
                tpd.show();
            }
        });

        // Save task button
        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etTaskName.getText().toString().trim();
                String category = etTaskCategory.getText().toString().trim();
                String priority = spinnerPriority.getSelectedItem().toString();
                String dueDate = etDueDate.getText().toString().trim();
                String dueTime = etDueTime.getText().toString().trim();

                if (name.isEmpty() || category.isEmpty() || dueDate.isEmpty() || dueTime.isEmpty()) {
                    Toast.makeText(AddTaskActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Initial status Pending, initial score 0
                Task task = new Task(name, category, priority, dueDate, dueTime, "Pending", 0);
                long id = dbHelper.addTask(task);

                if (id > 0) {
                    Toast.makeText(AddTaskActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity and return to MainActivity
                } else {
                    Toast.makeText(AddTaskActivity.this, "Error saving task", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
