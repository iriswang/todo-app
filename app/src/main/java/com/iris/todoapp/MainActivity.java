package com.iris.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int EDIT_REQUEST_CODE = 20;

    ArrayList<TodoItem> todoItems;
    ArrayAdapter<TodoItem> aTodoAdapter;
    ListView lvItems;
    EditText etEditText;
    TodoItemDatabaseDAO todoItemDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TodoItemDatabaseHelper todoItemDBHelper = new TodoItemDatabaseHelper(this);
        this.todoItemDAO = new TodoItemDatabaseDAO(todoItemDBHelper.getWritableDatabase());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateArrayItems();

        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aTodoAdapter);
        lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                long id) {
                TodoItem itemToDelete = todoItems.get(position);
                todoItems.remove(position);
                aTodoAdapter.notifyDataSetChanged();
                todoItemDAO.deleteItem(itemToDelete._id);
                return true;
            }
        });
        lvItems.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchEditItemView(position);
            }
        });

        etEditText = (EditText) findViewById(R.id.etEditText);
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
            aTodoAdapter.add(item);
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
                try {
                    todoItemDAO.updateItem(newItem);
                    int position = data.getExtras().getInt("position");
                    todoItems.set(position, newItem);
                    aTodoAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(this, "Item must at least contain one character!",
                        Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void populateArrayItems() {
        todoItems = todoItemDAO.getAllItems();
        aTodoAdapter = new ArrayAdapter<TodoItem>(
            this, android.R.layout.simple_list_item_1, todoItems);
    }

}
