package com.helloworld.inclass15;

public class TodoClass {
    String date, priority, task_name;
    boolean checked;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "TodoClass{" +
                "date='" + date + '\'' +
                ", priority='" + priority + '\'' +
                ", task_name='" + task_name + '\'' +
                ", checked=" + checked +
                '}';
    }
}
