package com.iris.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int EDIT_REQUEST_CODE = 20;

    ArrayList<TodoItem> todoItems;
    ArrayList<TodoItem> finishedItems;
    TodoItemAdapter finishedItemAdapter;
    TodoItemAdapter todoItemAdapter;
    ListView todoItemsView;
    ListView finishedItemsView;
    EditText etEditText;
    TodoItemDatabaseDAO todoItemDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TodoItemDatabaseHelper todoItemDBHelper = new TodoItemDatabaseHelper(this);
        this.todoItemDAO = new TodoItemDatabaseDAO(todoItemDBHelper.getWritableDatabase());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateTodoListItems();

        todoItemsView = (ListView) findViewById(R.id.todoItems);
        finishedItemsView = (ListView) findViewById(R.id.finishedItems);

        setTodoItemsView(todoItemAdapter, todoItemsView, false);
        setTodoItemsView(finishedItemAdapter, finishedItemsView, true);

        etEditText = (EditText) findViewById(R.id.etEditText);
    }

    public void setTodoItemsView(TodoItemAdapter adapter, ListView view,
        final boolean completedItems) {
        view.setAdapter(adapter);
        view.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                long id) {
                TodoItem itemToDelete;
                if (completedItems) {
                    //TODO: replace this with other list
                    itemToDelete = finishedItems.get(position);
                    finishedItems.remove(position);
                    finishedItemAdapter.notifyDataSetChanged();
                } else {
                    itemToDelete = todoItems.get(position);
                    todoItems.remove(position);
                    todoItemAdapter.notifyDataSetChanged();
                }
                todoItemDAO.deleteItem(itemToDelete._id);
                return true;
            }
        });
        view.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchEditItemView(position);
            }
        });
    }

    public void launchEditItemView(int position) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra("list_position", position);
        i.putExtra("item", todoItems.get(position));
        startActivityForResult(i, EDIT_REQUEST_CODE);
    }

    public void onAddItem(View view) {
        String newItem = etEditText.getText().toString();
        try {
            TodoItem item = new TodoItem(newItem);
            todoItemDAO.addItem(item);
            todoItemAdapter.add(item);
            etEditText.setText("");
        } catch (Exception e) {
            Toast.makeText(this, "Item must at least contain one character!", Toast.LENGTH_SHORT)
                 .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                TodoItem newItem = (TodoItem) data.getSerializableExtra("new_item");
                int position = data.getExtras().getInt("position");
                if (newItem != todoItems.get(position)) {
                    try {
                        todoItemDAO.updateItem(newItem);
                        todoItems.remove(position);
                        todoItems.add(newItem);
                        todoItemAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Item must at least contain one character!",
                            Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void populateTodoListItems() {
        todoItems = todoItemDAO.getUnfinishedItems();
        todoItemAdapter = new TodoItemAdapter(this, todoItems);
        finishedItems = todoItemDAO.getCompletedItems();
        finishedItemAdapter = new TodoItemAdapter(this, finishedItems);
    }
}
