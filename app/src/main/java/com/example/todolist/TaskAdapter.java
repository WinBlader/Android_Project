package com.example.todolist;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList){
        this.context = context;
        this.taskList = taskList;
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
        holder.taskDetails.setText(task.getCategory() + " | " + task.getPriority() + " | " + task.getDueDate() + " " + task.getDueTime());
        holder.taskStatus.setText("Status: " + task.getStatus());

        // Set status color
        switch(task.getStatus()){
            case "Completed":
                holder.taskStatus.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "Pending":
                holder.taskStatus.setTextColor(Color.parseColor("#FFC107"));
                break;
            case "Failed":
                holder.taskStatus.setTextColor(Color.parseColor("#F44336"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder{
        TextView taskName, taskDetails, taskStatus;
        public TaskViewHolder(@NonNull View itemView){
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDetails = itemView.findViewById(R.id.taskDetails);
            taskStatus = itemView.findViewById(R.id.taskStatus);
        }
    }
}
