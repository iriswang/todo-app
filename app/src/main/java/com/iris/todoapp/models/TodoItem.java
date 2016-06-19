package com.iris.todoapp.models;

import com.google.common.collect.ImmutableMap;


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.qbusict.cupboard.annotation.Column;

/**
 * Created by iris on 3/25/16.
 */
public class TodoItem implements Serializable {

    public enum Priority {
        HIGH, MEDIUM, LOW;

        public static final List<Priority> ALL_PRIORITIES =
            Arrays.asList(HIGH, MEDIUM, LOW);

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

        public static final List<Status> ALL_STATUSES =
            Arrays.asList(Status.TODO, Status.DONE);
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

    public static TodoItem newInstance(TodoItem oldItem) {
        TodoItem newItem = new TodoItem(oldItem.title);
        newItem._id = oldItem._id;
        newItem.dueDate = oldItem.dueDate;
        newItem.priority = oldItem.priority;
        newItem.status = oldItem.status;
        newItem.tag = oldItem.tag;
        newItem.notes = oldItem.notes;
        return newItem;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof TodoItem))
            return false;
        TodoItem other = (TodoItem) obj;
        return _id == other._id
            && dueDate == other.dueDate
            && priority == other.priority
            && status == other.status
            && tag == other.tag
            && notes == other.notes;
    }
}


