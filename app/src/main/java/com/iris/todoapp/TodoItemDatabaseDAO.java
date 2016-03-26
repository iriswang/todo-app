package com.iris.todoapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;

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
