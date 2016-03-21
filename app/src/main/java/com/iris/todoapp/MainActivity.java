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


import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int EDIT_REQUEST_CODE = 20;

    ArrayList<String> todoItems;
    ArrayAdapter<String> aTodoAdapter;
    ListView lvItems;
    EditText etEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateArrayItems();
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aTodoAdapter);
        etEditText = (EditText) findViewById(R.id.etEditText);
        lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                long id) {
                todoItems.remove(position);
                aTodoAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });
        lvItems.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchEditItemView(position);
            }
        });
    }

    public void launchEditItemView(int position) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra("list_position", position);
        i.putExtra("item_text", todoItems.get(position));
        startActivityForResult(i, EDIT_REQUEST_CODE);
    }

    public void onAddItem(View view) {
        String newItem = etEditText.getText().toString();
        if (newItem.isEmpty()) {
            Toast.makeText(this, "Item must at least contain one character!", Toast.LENGTH_SHORT)
                 .show();
        } else {
            aTodoAdapter.add(newItem);
            etEditText.setText("");
            writeItems();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String newItem = data.getExtras().getString("new_item");
                int position = data.getExtras().getInt("position");
                todoItems.set(position, newItem);
                aTodoAdapter.notifyDataSetChanged();
                writeItems();
            }
        }
    }

    public void populateArrayItems() {
        readItems();
        aTodoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoItems);
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File file = new File (filesDir, "todo.txt");
        try {
            todoItems = new ArrayList<>(FileUtils.readLines(file));
        } catch (IOException e) {
            todoItems = new ArrayList<>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File file = new File (filesDir, "todo.txt");
        try {
            FileUtils.writeLines(file, todoItems);
        } catch (IOException e) {

        }
    }

}
