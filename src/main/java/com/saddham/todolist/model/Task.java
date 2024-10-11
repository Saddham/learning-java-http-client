package com.saddham.todolist.model;

public class Task {
    public int userId;
    public int id;
    public String title;
    public boolean completed;

    @Override
    public String toString() {
        return "Task{" +
                "userId='" + userId + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                '}';
    }
}
