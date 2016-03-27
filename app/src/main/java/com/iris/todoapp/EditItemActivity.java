package com.iris.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.iris.todoapp.TodoItem.Priority;


import java.util.ArrayList;
import java.util.List;

public class EditItemActivity extends AppCompatActivity {

    int position;
    TodoItem item;
    EditText editItem;
    Spinner prioritySpinner;
    List<String> priorities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        editItem = (EditText) findViewById(R.id.etEditItem);
        position = getIntent().getIntExtra("list_position", -1);

        item = (TodoItem) getIntent().getSerializableExtra("item");
        editItem.setText(item.title);

        createPrioritySpinner(item);
        prioritySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item.priority = priorities.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
}

    public void createPrioritySpinner(TodoItem item) {
        prioritySpinner = (Spinner) findViewById(R.id.prioritySpinner);
        priorities = new ArrayList<String>();
        priorities.add(Priority.HIGH.toString());
        priorities.add(Priority.MEDIUM.toString());
        priorities.add(Priority.LOW.toString());
        priorities.add("Select one");

        ArrayAdapter<String> priorityListAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, priorities) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }

        };

        priorityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityListAdapter);
        if (item.priority == null) {
            prioritySpinner.setSelection(priorityListAdapter.getCount());
        } else {
            prioritySpinner.setSelection(priorities.indexOf(item.priority));
        }
    }

    public void onSaveItem(View view) {
        String newItemText = editItem.getText().toString();
        item.title = newItemText;
        Intent data = new Intent();
        data.putExtra("position", position);
        data.putExtra("new_item", item);
        setResult(RESULT_OK, data);
        finish();
    }

}
