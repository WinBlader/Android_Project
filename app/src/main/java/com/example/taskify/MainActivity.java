package com.example.taskify;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddTask;
    private TaskDBHelper dbHelper;
    private TaskAdapter taskAdapter;
    private MaterialButton btnFilter;
    private TaskFilter taskFilter;
    private List<Task> allTasks;
    private List<Task> taskList;
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
        btnFilter = findViewById(R.id.btnFilterTasks);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvFailedCount = findViewById(R.id.tvFailedCount);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_info) {
                    openStats();
                    return true;
                } else if (item.getItemId() == R.id.action_theme) {
                    toggleTheme();
                    return true;
                }
                return false;
            }
        });

        dbHelper = new TaskDBHelper(this);
        taskFilter = new TaskFilter();
        allTasks = new ArrayList<>();
        // Check for overdue tasks and mark them as failed
        dbHelper.markOverdueTasksAsFailed();
        allTasks.addAll(dbHelper.getAllTasks());
        taskList = new ArrayList<>(allTasks);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskList);
        taskAdapter.setOnTaskDeleteListener(new TaskAdapter.OnTaskDeleteListener() {
            @Override
            public void onTaskDeleted(int position) {
                refreshTasksFromDatabase();
            }
        });
        recyclerView.setAdapter(taskAdapter);
        applyFilter();

        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open AddTaskActivity
                startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check for overdue tasks and mark them as failed
        dbHelper.markOverdueTasksAsFailed();

        // Refresh task list after returning from AddTaskActivity
        refreshTasksFromDatabase();
    }

    private void updateHeaderCounts() {
        int completed = 0, pending = 0, failed = 0;
        for (Task t : taskList) {
            if ("Completed".equals(t.getStatus())) {
                completed++;
            } else if ("Failed".equals(t.getStatus())) {
                failed++;
            } else {
                pending++;
            }
        }
        if (tvCompletedCount != null) {
            tvCompletedCount.setText("Completed: " + completed);
        }
        if (tvPendingCount != null) {
            tvPendingCount.setText("Pending: " + pending);
        }
        if (tvFailedCount != null) {
            tvFailedCount.setText("Failed: " + failed);
        }
    }

    private void updateFilterLabel() {
        if (btnFilter != null) {
            btnFilter.setText(taskFilter.getDisplayLabel(getString(R.string.filter_all_tasks)));
        }
    }

    private void openStats() {
        Intent intent = new Intent(MainActivity.this, StatsActivity.class);
        taskFilter.writeToIntent(intent);
        startActivity(intent);
    }

    private void refreshTasksFromDatabase() {
        allTasks.clear();
        allTasks.addAll(dbHelper.getAllTasks());
        applyFilter();
    }

    private void applyFilter() {
        List<Task> filtered = taskFilter.apply(allTasks);
        taskList.clear();
        if (filtered != null) {
            taskList.addAll(filtered);
        }
        taskAdapter.notifyDataSetChanged();
        updateHeaderCounts();
        updateFilterLabel();
    }

    private void showFilterDialog() {
        final String[] options = new String[]{
                getString(R.string.filter_all_tasks),
                getString(R.string.filter_today),
                getString(R.string.filter_this_month),
                getString(R.string.filter_this_year),
                getString(R.string.filter_pick_date),
                getString(R.string.filter_pick_month),
                getString(R.string.filter_pick_year)
        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.filter_dialog_title))
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar now = Calendar.getInstance();
                        switch (which) {
                            case 0: // All
                                taskFilter.setAll();
                                applyFilter();
                                break;
                            case 1: // Today
                                taskFilter.setDate(
                                        now.get(Calendar.YEAR),
                                        now.get(Calendar.MONTH),
                                        now.get(Calendar.DAY_OF_MONTH)
                                );
                                applyFilter();
                                break;
                            case 2: // This month
                                taskFilter.setMonth(
                                        now.get(Calendar.YEAR),
                                        now.get(Calendar.MONTH)
                                );
                                applyFilter();
                                break;
                            case 3: // This year
                                taskFilter.setYear(now.get(Calendar.YEAR));
                                applyFilter();
                                break;
                            case 4: // Pick date
                                showDatePicker();
                                break;
                            case 5: // Pick month
                                showMonthYearPicker();
                                break;
                            case 6: // Pick year
                                showYearPicker();
                                break;
                        }
                    }
                })
                .show();
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                taskFilter.setDate(year, month, dayOfMonth);
                applyFilter();
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showMonthYearPicker() {
        Calendar now = Calendar.getInstance();
        final android.widget.NumberPicker monthPicker = new android.widget.NumberPicker(this);
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        String[] months = new DateFormatSymbols().getMonths();
        if (months.length > 12) {
            String[] first12 = new String[12];
            System.arraycopy(months, 0, first12, 0, 12);
            months = first12;
        }
        monthPicker.setDisplayedValues(months);
        monthPicker.setValue(now.get(Calendar.MONTH));

        final android.widget.NumberPicker yearPicker = new android.widget.NumberPicker(this);
        int currentYear = now.get(Calendar.YEAR);
        int minYear = Math.max(1970, currentYear - 20);
        int maxYear = currentYear + 20;
        yearPicker.setMinValue(minYear);
        yearPicker.setMaxValue(maxYear);
        yearPicker.setValue(currentYear);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                0,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        layout.addView(monthPicker, params);
        layout.addView(yearPicker, params);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.filter_select_month_title))
                .setView(layout)
                .setPositiveButton(getString(R.string.filter_action_apply), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskFilter.setMonth(yearPicker.getValue(), monthPicker.getValue());
                        applyFilter();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showYearPicker() {
        Calendar now = Calendar.getInstance();
        final android.widget.NumberPicker yearPicker = new android.widget.NumberPicker(this);
        int currentYear = now.get(Calendar.YEAR);
        int minYear = Math.max(1970, currentYear - 20);
        int maxYear = currentYear + 20;
        yearPicker.setMinValue(minYear);
        yearPicker.setMaxValue(maxYear);
        yearPicker.setValue(currentYear);

        android.widget.FrameLayout layout = new android.widget.FrameLayout(this);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        layout.addView(yearPicker, params);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.filter_select_year_title))
                .setView(layout)
                .setPositiveButton(getString(R.string.filter_action_apply), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskFilter.setYear(yearPicker.getValue());
                        applyFilter();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
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
