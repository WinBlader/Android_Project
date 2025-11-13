package com.example.taskify;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TextView tvScore;
    private MaterialButton btnFilter;
    private TaskDBHelper dbHelper;
    private TaskFilter taskFilter;
    private List<Task> allTasks;
    private List<Task> filteredTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply theme before setting content view
        ThemeManager.getInstance(this).applyTheme(this);

        setContentView(R.layout.activity_stats);

        pieChart = findViewById(R.id.pieChart);
        tvScore = findViewById(R.id.tvScore);
        btnFilter = findViewById(R.id.btnFilterStats);

        dbHelper = new TaskDBHelper(this);
        taskFilter = new TaskFilter();
        taskFilter.applyFromIntent(getIntent());
        allTasks = new ArrayList<>();
        filteredTasks = new ArrayList<>();

        btnFilter.setOnClickListener(v -> showFilterDialog());

        refreshTasksFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTasksFromDatabase();
    }

    private void refreshTasksFromDatabase() {
        allTasks.clear();
        allTasks.addAll(dbHelper.getAllTasks());
        applyFilter();
    }

    private void applyFilter() {
        List<Task> filtered = taskFilter.apply(allTasks);
        filteredTasks.clear();
        if (filtered != null) {
            filteredTasks.addAll(filtered);
        }
        updateFilterLabel();
        showStats();
    }

    private void updateFilterLabel() {
        if (btnFilter != null) {
            btnFilter.setText(taskFilter.getDisplayLabel(getString(R.string.filter_all_tasks)));
        }
    }

    private void showStats() {
        int completed = 0, pending = 0, failed = 0;
        int totalScore = 0;

        for (Task task : filteredTasks) {
            totalScore += task.getScore();
            switch (task.getStatus()) {
                case "Completed":
                    completed++;
                    break;
                case "Pending":
                    pending++;
                    break;
                case "Failed":
                    failed++;
                    break;
            }
        }

        // Calculate percentages as floats
        int totalTasks = completed + pending + failed;
        float completedPercent = totalTasks > 0 ? (float) completed / totalTasks * 100 : 0;
        float pendingPercent = totalTasks > 0 ? (float) pending / totalTasks * 100 : 0;
        float failedPercent = totalTasks > 0 ? (float) failed / totalTasks * 100 : 0;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(completedPercent, "Completed"));
        entries.add(new PieEntry(pendingPercent, "Pending"));
        entries.add(new PieEntry(failedPercent, "Failed"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        // Set explicit colors: Completed (green), Pending (yellow), Failed (red)
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(0xFF4CAF50); // green
        colors.add(0xFFFFC107); // yellow
        colors.add(0xFFF44336); // red
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setValueTextSize(14f);
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.PercentFormatter());

        PieData data = new PieData(dataSet);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(14f);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setCenterText(getString(R.string.stats_center_text, totalScore));
        pieChart.setCenterTextSize(18f);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getLegend().setTextSize(14f);
        pieChart.animateY(1000);
        pieChart.invalidate();
        // Keep bottom scores as integers
        tvScore.setText(getString(R.string.stats_summary_counts, completed, pending, failed));
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
                            case 0:
                                taskFilter.setAll();
                                applyFilter();
                                break;
                            case 1:
                                taskFilter.setDate(
                                        now.get(Calendar.YEAR),
                                        now.get(Calendar.MONTH),
                                        now.get(Calendar.DAY_OF_MONTH)
                                );
                                applyFilter();
                                break;
                            case 2:
                                taskFilter.setMonth(
                                        now.get(Calendar.YEAR),
                                        now.get(Calendar.MONTH)
                                );
                                applyFilter();
                                break;
                            case 3:
                                taskFilter.setYear(now.get(Calendar.YEAR));
                                applyFilter();
                                break;
                            case 4:
                                showDatePicker();
                                break;
                            case 5:
                                showMonthYearPicker();
                                break;
                            case 6:
                                showYearPicker();
                                break;
                        }
                    }
                })
                .show();
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            taskFilter.setDate(year, month, dayOfMonth);
            applyFilter();
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
}

