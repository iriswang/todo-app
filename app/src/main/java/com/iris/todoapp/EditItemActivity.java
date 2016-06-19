package com.iris.todoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.iris.todoapp.TodoItem.Priority;
import com.iris.todoapp.TodoItem.Status;

public class EditItemActivity extends AppCompatActivity {

    int groupPosition;
    int childPosition;
    int requestType;
    TodoItem originalItem;
    TodoItem item;
    EditText editItem;
    EditText editNotes;
    Spinner prioritySpinner;
    Spinner statusSpinner;
    TodoItemDatabaseDAO todoItemDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TodoItemDatabaseHelper todoItemDBHelper = new TodoItemDatabaseHelper(this);
        this.todoItemDAO = new TodoItemDatabaseDAO(todoItemDBHelper.getWritableDatabase());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        editItem = (EditText) findViewById(R.id.etEditItem);
        editItem.setSelection(editItem.getText().length());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editNotes = (EditText) findViewById(R.id.etEditNotes);
        editNotes.setSelection(editNotes.getText().length());

        groupPosition = getIntent().getIntExtra(TodoAppConstants.GROUP_POSITION, -1);
        childPosition = getIntent().getIntExtra(TodoAppConstants.CHILD_POSITION, -1);
        requestType = (int) getIntent().getSerializableExtra("request_type");

        if (requestType == TodoAppConstants.ADD_REQUEST_CODE) {
            item = new TodoItem();
            editItem.setText("");
            editNotes.setText("");
        } else {
            item = (TodoItem) getIntent().getSerializableExtra("item");
            editItem.setText(item.title);
            editNotes.setText(item.notes);
        }

        originalItem = TodoItem.newInstance(item);
        createPrioritySpinner(item.priority);
        createStatusSpinner(item.status);
    }


    private void handleInvalidEdit() {
        AlertDialog.Builder invalidEditBuilder = new AlertDialog.Builder(EditItemActivity.this);
        invalidEditBuilder.setMessage("Item must at least contain one character!");
        invalidEditBuilder.setCancelable(true);

        invalidEditBuilder.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        invalidEditBuilder.setNegativeButton(
            "Cancel",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                    dialog.cancel();
                }
            });
        AlertDialog invalidTitleAlert = invalidEditBuilder.create();
        invalidTitleAlert.show();
    }

    public void createPrioritySpinner(Priority originalPriority) {
        prioritySpinner = (Spinner) findViewById(R.id.prioritySpinner);

        ArrayAdapter<Priority> priorityListAdapter = new ArrayAdapter<Priority>(this,
            android.R.layout.simple_spinner_item, TodoItem.Priority.ALL_PRIORITIES);

        priorityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityListAdapter);
        prioritySpinner.setSelection(Priority.ALL_PRIORITIES.indexOf(originalPriority));
        prioritySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item.priority = Priority.ALL_PRIORITIES.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void createStatusSpinner(Status originalStatus) {
        statusSpinner = (Spinner) findViewById(R.id.statusSpinner);

        ArrayAdapter<Status> statusArrayAdapter = new ArrayAdapter<Status>(this,
            android.R.layout.simple_spinner_item, Status.ALL_STATUSES);

        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusArrayAdapter);
        statusSpinner.setSelection(Status.ALL_STATUSES.indexOf(originalStatus));
        statusSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item.status = Status.ALL_STATUSES.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onSaveItem() {
        item.title = editItem.getText().toString();
        item.notes =  editNotes.getText().toString();
        try {
            Intent data = new Intent();
            if (requestType == TodoAppConstants.ADD_REQUEST_CODE) {
               todoItemDAO.addItem(item);
            } else {
                todoItemDAO.updateItem(item);
                data.putExtra(TodoAppConstants.GROUP_POSITION, groupPosition);
                data.putExtra(TodoAppConstants.CHILD_POSITION, childPosition);
            }
            data.putExtra("new_item", item);
            setResult(RESULT_OK, data);
            finish();
        } catch (Exception e) {
            handleInvalidEdit();
        }
    }

    private void handleExitWithChanges() {
        AlertDialog.Builder invalidEditBuilder = new AlertDialog.Builder(EditItemActivity.this);
        invalidEditBuilder.setMessage("Discard your changes?");
        invalidEditBuilder.setCancelable(true);

        invalidEditBuilder.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                    dialog.cancel();
                }
            });

        invalidEditBuilder.setNegativeButton(
            "CANCEL",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        AlertDialog invalidTitleAlert = invalidEditBuilder.create();
        invalidTitleAlert.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_item_memu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.cancel_action:
                if (originalItem.equals(item)
                    && editItem.getText().toString().equals(originalItem.title)
                    && editNotes.getText().toString().equals(originalItem.notes)) {
                    finish();
                } else {
                   handleExitWithChanges();
                }
                return true;
            case R.id.save_action:
                onSaveItem();
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }



}
