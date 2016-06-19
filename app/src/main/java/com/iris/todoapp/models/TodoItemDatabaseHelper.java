package com.iris.todoapp.models;

/**
 * Created by iris on 3/25/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iris.todoapp.models.TodoItem;


import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class TodoItemDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todoItem.db";
    private static final int DATABASE_VERSION = 1;

    public TodoItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static {
        // register our models
        cupboard().register(TodoItem.class);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created
        cupboard().withDatabase(db).createTables();
        // add indexes and other database tweaks in this method if you want

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }

}
