package com.example.taskify;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TextView tvScore;
    private TaskDBHelper dbHelper;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        pieChart = findViewById(R.id.pieChart);
        tvScore = findViewById(R.id.tvScore);

        dbHelper = new TaskDBHelper(this);
        taskList = dbHelper.getAllTasks();

        showStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskList.clear();
        taskList.addAll(dbHelper.getAllTasks());
        showStats();
    }

    private void showStats() {
        int completed = 0, pending = 0, failed = 0;
        int totalScore = 0;

        for (Task task : taskList) {
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

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(completed, "Completed"));
        entries.add(new PieEntry(pending, "Pending"));
        entries.add(new PieEntry(failed, "Failed"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        // Set explicit colors: Completed (green), Pending (yellow), Failed (red)
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(0xFF4CAF50); // green
        colors.add(0xFFFFC107); // yellow
        colors.add(0xFFF44336); // red
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(14f);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setCenterText("Score: " + totalScore);
        pieChart.setCenterTextSize(18f);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getLegend().setTextSize(14f);
        pieChart.animateY(1000);
        pieChart.invalidate();
        tvScore.setText("Completed: " + completed + "  Pending: " + pending + "  Failed: " + failed);
    }
}


