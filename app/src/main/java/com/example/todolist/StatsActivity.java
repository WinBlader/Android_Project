package com.example.todolist;

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
        if (completed > 0) entries.add(new PieEntry(completed, "Completed"));
        if (pending > 0) entries.add(new PieEntry(pending, "Pending"));
        if (failed > 0) entries.add(new PieEntry(failed, "Failed"));

        PieDataSet dataSet = new PieDataSet(entries, "Task Status");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Tasks");
        pieChart.setCenterTextSize(16f);
        pieChart.animateY(1000);
        pieChart.invalidate();

        tvScore.setText("Score: " + totalScore);
    }
}
