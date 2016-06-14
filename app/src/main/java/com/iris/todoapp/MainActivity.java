package com.iris.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int EDIT_REQUEST_CODE = 20;

    private final String COMPLETED_ITEMS = "Completed";
    private final String UNFINISHED_ITEMS = "To Do";


    TodoItemExpandableListAdapter todoItemListAdapter;
    ExpandableListView expListView;
    List<String> todoItemListDataHeaders;
    HashMap<String, List<TodoItem>> todoItemsListDataChildren;

    EditText etEditText;
    TodoItemDatabaseDAO todoItemDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TodoItemDatabaseHelper todoItemDBHelper = new TodoItemDatabaseHelper(this);
        this.todoItemDAO = new TodoItemDatabaseDAO(todoItemDBHelper.getWritableDatabase());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        prepareListData();
        setUpExpListViewClickHandlers();
        todoItemListAdapter = new TodoItemExpandableListAdapter(this, todoItemListDataHeaders,
            todoItemsListDataChildren);

        expListView.setAdapter(todoItemListAdapter);
        etEditText = (EditText) findViewById(R.id.etEditText);
    }

    private void prepareListData() {
        todoItemListDataHeaders = new ArrayList<String>();
        todoItemsListDataChildren = new HashMap<String, List<TodoItem>>();
        todoItemListDataHeaders.add(UNFINISHED_ITEMS);
        todoItemListDataHeaders.add(COMPLETED_ITEMS);
        todoItemsListDataChildren.put(UNFINISHED_ITEMS, todoItemDAO.getUnfinishedItems());
        todoItemsListDataChildren.put(COMPLETED_ITEMS, todoItemDAO.getCompletedItems());
    }

    private void setUpExpListViewClickHandlers() {

    }

    public void onAddItem(View view) {
        String newItem = etEditText.getText().toString();
        try {
            TodoItem item = new TodoItem(newItem);
            todoItemDAO.addItem(item);
            todoItemsListDataChildren.get(UNFINISHED_ITEMS).add(item);
            etEditText.setText("");
            todoItemListAdapter.notifyDataSetChanged();
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
//                if (newItem != todoItems.get(position)) {
//                    try {
//                        todoItemDAO.updateItem(newItem);
//                        todoItems.remove(position);
//                        todoItems.add(newItem);
//                        todoItemListAdapter.notifyDataSetChanged();
//                    } catch (Exception e) {
//                        Toast.makeText(this, "Item must at least contain one character!",
//                            Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        }
    }
}
