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

    public String priority = null;

    public String status = Status.TODO.toString();

    public Long tag = null;

    public String title;

    public TodoItem() {
    }

    public TodoItem(String title) {
        this.title = title;
    }
}

