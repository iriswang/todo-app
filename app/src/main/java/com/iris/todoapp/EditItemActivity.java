package com.iris.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


import com.iris.todoapp.TodoItem.Priority;
import com.iris.todoapp.TodoItem.Status;

import java.util.Arrays;
import java.util.List;

public class EditItemActivity extends AppCompatActivity {

    private final List<Priority> priorities =
        Arrays.asList(Priority.HIGH, Priority.MEDIUM, Priority.LOW);
    private final List<Status> statuses =
        Arrays.asList(Status.TODO, Status.DONE);
    int groupPosition;
    int childPosition;
    TodoItem item;
    EditText editItem;
    Spinner prioritySpinner;
    Spinner statusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        editItem = (EditText) findViewById(R.id.etEditItem);
        groupPosition = getIntent().getIntExtra(TodoAppConstants.GROUP_POSITION, -1);
        childPosition = getIntent().getIntExtra(TodoAppConstants.CHILD_POSITION, -1);

        item = (TodoItem) getIntent().getSerializableExtra("item");
        editItem.setText(item.title);

        createPrioritySpinner(item.priority);
        createStatusSpinner(item.status);
}

    public void createPrioritySpinner(Priority originalPriority) {
        prioritySpinner = (Spinner) findViewById(R.id.prioritySpinner);

        ArrayAdapter<Priority> priorityListAdapter = new ArrayAdapter<Priority>(this,
            android.R.layout.simple_spinner_item, priorities);

        priorityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityListAdapter);
        prioritySpinner.setSelection(priorities.indexOf(originalPriority));
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

    public void createStatusSpinner(Status originalStatus) {
        statusSpinner = (Spinner) findViewById(R.id.statusSpinner);

        ArrayAdapter<Status> statusArrayAdapter = new ArrayAdapter<Status>(this,
            android.R.layout.simple_spinner_item, statuses);

        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusArrayAdapter);
        statusSpinner.setSelection(statuses.indexOf(originalStatus));
        statusSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item.status = statuses.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onSaveItem(View view) {
        String newItemText = editItem.getText().toString();
        item.title = newItemText;
        Intent data = new Intent();
        data.putExtra(TodoAppConstants.GROUP_POSITION, groupPosition);
        data.putExtra(TodoAppConstants.CHILD_POSITION, childPosition);
        data.putExtra("new_item", item);
        setResult(RESULT_OK, data);
        finish();
    }

}
