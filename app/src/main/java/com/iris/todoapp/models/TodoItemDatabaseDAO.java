package com.iris.todoapp.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.iris.todoapp.models.TodoItem;
import com.iris.todoapp.models.TodoItem.Status;


import java.util.ArrayList;
import java.util.Calendar;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by iris on 3/26/16.
 */
public class TodoItemDatabaseDAO {

    SQLiteDatabase db;

    public TodoItemDatabaseDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long addItem(TodoItem item) throws Exception{
        if (validateItem(item)) {
            return cupboard().withDatabase(db).put(item);
        } else {
            throw new Exception("Error with fields");
        }
    }

    public void deleteItem(long id) {
        cupboard().withDatabase(db).delete(TodoItem.class, id);
    }

    private Long getTodayInMilliseconds() {
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        return cal.getTimeInMillis();
    }

    public ArrayList<TodoItem> getTodaysCompletedItems() {
        Cursor items = cupboard().withDatabase(db).query(TodoItem.class).getCursor();
        ArrayList<TodoItem> result = new ArrayList<>();
        QueryResultIterable<TodoItem> itemsIterable =
            cupboard().withDatabase(db).query(TodoItem.class)
                      .withSelection("status = ? AND dueDate = ?",
                          Status.DONE.toString(),
                          getTodayInMilliseconds().toString())
                      .query();
        for (TodoItem item: itemsIterable) {
            result.add(item);
        }
        return result;
    }

    public ArrayList<TodoItem> getCompletedItems() {
        Cursor items = cupboard().withDatabase(db).query(TodoItem.class).getCursor();
        ArrayList<TodoItem> result = new ArrayList<>();
        QueryResultIterable<TodoItem> itemsIterable =
            cupboard().withDatabase(db).query(TodoItem.class).withSelection(
                "status = ?", Status.DONE.toString()).query();
        for (TodoItem item: itemsIterable) {
            result.add(item);
        }
        return result;
    }

    public ArrayList<TodoItem> getTodaysUnfinishedItems() {
        ArrayList<TodoItem> result = new ArrayList<>();
        QueryResultIterable<TodoItem> itemsIterable =
            cupboard().withDatabase(db).query(TodoItem.class)
                      .withSelection("status = ? AND dueDate = ?",
                          Status.TODO.toString(),
                          getTodayInMilliseconds().toString())
                      .query();
        for (TodoItem item: itemsIterable) {
            result.add(item);
        }
        return result;
    }

    public ArrayList<TodoItem> getUnfinishedItems() {
        ArrayList<TodoItem> result = new ArrayList<>();
        QueryResultIterable<TodoItem> itemsIterable =
            cupboard().withDatabase(db).query(TodoItem.class).withSelection(
                "status = ?", Status.TODO.toString()).query();
        for (TodoItem item: itemsIterable) {
            result.add(item);
        }
        return result;
    }

    public ArrayList<TodoItem> getAllItems() {
        Cursor items = cupboard().withDatabase(db).query(TodoItem.class).getCursor();
        QueryResultIterable<TodoItem> itemsIterable =
            cupboard().withCursor(items).iterate(TodoItem.class);
        ArrayList<TodoItem> result = new ArrayList<>();
        for (TodoItem item: itemsIterable) {
            result.add(item);
        }
        return result;
    }

    public void updateItem(TodoItem item) throws Exception {
        if (validateItem(item)) {
            cupboard().withDatabase(db).put(item);
        } else {
            throw new Exception("Error with fields");
        }
    }

    private boolean validateItem(TodoItem item){
        if (item.title.isEmpty()) {
            return false;
        }
        return true;
    }

}
