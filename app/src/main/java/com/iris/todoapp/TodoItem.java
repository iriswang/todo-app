package com.iris.todoapp;

import com.google.common.collect.ImmutableMap;


import java.io.Serializable;
import java.util.Map;

import nl.qbusict.cupboard.annotation.Column;

/**
 * Created by iris on 3/25/16.
 */
public class TodoItem implements Serializable{

    public enum Priority {
        HIGH, MEDIUM, LOW;

        public static int sort(Priority lhs, Priority rhs) {
            Map<Priority, Integer> priorityValues = ImmutableMap.of(
                HIGH, 0,
                MEDIUM, 1,
                LOW, 2
            );
            return priorityValues.get(lhs).compareTo(priorityValues.get(rhs));
        }
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

