package com.example.taskify;

public class Task {
    private long id;
    private String name, category, priority, dueDate, dueTime, status;
    private int score;

    public Task(String name, String category, String priority, String dueDate, String dueTime, String status, int score) {
        this.name = name;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.status = status;
        this.score = score;
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    public String getDueDate() { return dueDate; }
    public String getDueTime() { return dueTime; }
    public String getStatus() { return status; }
    public int getScore() { return score; }

    public void setStatus(String status) { this.status = status; }
    public void setScore(int score) { this.score = score; }
}


