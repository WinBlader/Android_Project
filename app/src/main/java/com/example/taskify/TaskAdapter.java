package com.example.taskify;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;
    private OnTaskDeleteListener deleteListener;

    public interface OnTaskDeleteListener {
        void onTaskDeleted(int position);
    }

    public TaskAdapter(Context context, List<Task> taskList){
        this.context = context;
        this.taskList = taskList;
    }

    public void setOnTaskDeleteListener(OnTaskDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskName.setText(task.getName());
        String dayOfWeek = getDayOfWeek(task.getDueDate());
        holder.taskDetails.setText(task.getCategory() + " | " + task.getPriority() + " | " + dayOfWeek + ", " + task.getDueDate() + " " + task.getDueTime());
        holder.taskStatus.setText("Status: " + task.getStatus());

        // Set status color and button state
        switch(task.getStatus()){
            case "Completed":
                holder.taskStatus.setTextColor(Color.parseColor("#4CAF50"));
                holder.btnMarkComplete.setEnabled(false);
                holder.btnMarkComplete.setText("Completed");
                holder.btnMarkComplete.setAlpha(0.6f);
                break;
            case "Pending":
                holder.taskStatus.setTextColor(Color.parseColor("#FFC107"));
                holder.btnMarkComplete.setEnabled(true);
                holder.btnMarkComplete.setText("Mark Complete");
                holder.btnMarkComplete.setAlpha(1.0f);
                break;
            case "Failed":
                holder.taskStatus.setTextColor(Color.parseColor("#F44336"));
                holder.btnMarkComplete.setEnabled(true);
                holder.btnMarkComplete.setText("Mark Complete");
                holder.btnMarkComplete.setAlpha(1.0f);
                break;
        }

        holder.btnMarkComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.getStatus().equals("Completed")) {
                    return; // Do nothing if already completed
                }
                TaskDBHelper db = new TaskDBHelper(context);
                db.updateTaskStatusAndScore(task.getId(), "Completed", 1);
                task.setStatus("Completed");
                task.setScore(1);
                
                // Cancel alarm for completed task
                TaskAlarmManager alarmManager = new TaskAlarmManager(context);
                alarmManager.cancelTaskReminder(task.getId());
                
                notifyItemChanged(holder.getAdapterPosition());
            }
        });


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel alarm for deleted task
                TaskAlarmManager alarmManager = new TaskAlarmManager(context);
                alarmManager.cancelTaskReminder(task.getId());
                
                TaskDBHelper db = new TaskDBHelper(context);
                db.deleteTask(task.getId());
                int position = holder.getAdapterPosition();
                taskList.remove(position);
                notifyItemRemoved(position);
                if (deleteListener != null) {
                    deleteListener.onTaskDeleted(position);
                }
            }
        });
    }

    private String getDayOfWeek(String dateString){
        // Expecting dd/MM/yyyy
        SimpleDateFormat input = new SimpleDateFormat("d/M/yyyy");
        SimpleDateFormat output = new SimpleDateFormat("EEE");
        try{
            Date date = input.parse(dateString);
            return output.format(date);
        }catch (ParseException e){
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder{
        TextView taskName, taskDetails, taskStatus;
        Button btnMarkComplete, btnDelete;
        public TaskViewHolder(@NonNull View itemView){
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDetails = itemView.findViewById(R.id.taskDetails);
            taskStatus = itemView.findViewById(R.id.taskStatus);
            btnMarkComplete = itemView.findViewById(R.id.btnMarkComplete);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}


