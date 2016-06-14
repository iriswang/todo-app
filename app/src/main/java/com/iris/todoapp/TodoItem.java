package com.iris.todoapp;

import java.io.Serializable;

import nl.qbusict.cupboard.annotation.Column;

/**
 * Created by iris on 3/25/16.
 */
public class TodoItem implements Serializable{

    public enum Priority {
        HIGH, MEDIUM, LOW;
    }

    public enum Status {
        TODO, DONE;
    }

    public Long _id;

    @Column("due_date")
    public Long dueDate = null;

    public String notes = "";

    public Priority priority = Priority.MEDIUM;

    public Status status = Status.TODO;

    public Long tag = null;

    public String title;

    public TodoItem() {
    }

    public TodoItem(String title) {
        this.title = title;
    }
}

