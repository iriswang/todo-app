package com.iris.todoapp.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.iris.todoapp.R;
import com.iris.todoapp.views.dialogs.ToDoAppDatePickerDialog;
import com.iris.todoapp.utils.TodoAppConstants;
import com.iris.todoapp.models.TodoItem;
import com.iris.todoapp.models.TodoItem.Priority;
import com.iris.todoapp.models.TodoItem.Status;
import com.iris.todoapp.models.TodoItemDatabaseDAO;
import com.iris.todoapp.models.TodoItemDatabaseHelper;


import java.util.Calendar;
import java.util.Date;

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
    TextView tvDueDate;
    ToDoAppDatePickerDialog dueDatePickerDialog;
    TodoItemDatabaseDAO todoItemDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TodoItemDatabaseHelper todoItemDBHelper = new TodoItemDatabaseHelper(this);
        this.todoItemDAO = new TodoItemDatabaseDAO(todoItemDBHelper.getWritableDatabase());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        editItem = (EditText) findViewById(R.id.etEditItem);
        editItem.setSelection(editItem.getText().length());

        editNotes = (EditText) findViewById(R.id.etEditNotes);
        editNotes.setSelection(editNotes.getText().length());

        groupPosition = getIntent().getIntExtra(TodoAppConstants.GROUP_POSITION, -1);
        childPosition = getIntent().getIntExtra(TodoAppConstants.CHILD_POSITION, -1);
        requestType = (int) getIntent().getSerializableExtra("request_type");

        setUpToolBar();

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
        setUpDateViews();
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (requestType == TodoAppConstants.ADD_REQUEST_CODE) {
            toolbar.setTitle("Add Item");
        } else if (requestType == TodoAppConstants.EDIT_REQUEST_CODE) {
            toolbar.setTitle("Edit Item");
        }
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originalItem.equals(item)
                    && editItem.getText().toString().equals(originalItem.title)
                    && editNotes.getText().toString().equals(originalItem.notes)) {
                    finish();
                } else {
                    handleExitWithChanges();
                }
            }
        });
    }

    private Long convertCalendarToMilliseconds(Calendar cal) {
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        return cal.getTimeInMillis();
    }

    private Calendar convertMillisecondsToCalendar(Long milliseconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        return cal;
    }

    private void setUpDateViews() {
        tvDueDate = (TextView) findViewById(R.id.tvActualDate);
        final Calendar dueDate;
        if (item.dueDate == null) {
            tvDueDate.setText("Set a due date");
            dueDate = Calendar.getInstance();
        } else {
            dueDate = convertMillisecondsToCalendar(item.dueDate);
            String dateString = (String)
                android.text.format.DateFormat.format("MMMM dd", item.dueDate);
            tvDueDate.setText(dateString);
            tvDueDate.setTextColor(
                getApplicationContext().getResources()
                                       .getColor(R.color.colorBlack));
        }

        dueDatePickerDialog = new ToDoAppDatePickerDialog(this,
            new OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                }
            }, dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH),
            dueDate.get(Calendar.DAY_OF_MONTH));

        dueDatePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "SET",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(dueDatePickerDialog.getDatePicker().getYear(),
                        dueDatePickerDialog.getDatePicker().getMonth(),
                        dueDatePickerDialog.getDatePicker().getDayOfMonth());
                    item.dueDate = convertCalendarToMilliseconds(newDate);
                    String dateString = (String)
                        android.text.format.DateFormat.format("MMMM dd", item.dueDate);
                    tvDueDate.setText(dateString);
                    tvDueDate.setTextColor(
                        getApplicationContext().getResources()
                            .getColor(R.color.colorBlack));
                    dialog.cancel();
                }
            });

        dueDatePickerDialog.getDatePicker().updateDate(dueDate.get(Calendar.YEAR),
            dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH));

        tvDueDate.setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View v) {
                   dueDatePickerDialog.show();
                }
            }
        );

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

    private void handleDeleteTask() {
        AlertDialog.Builder deleteTaskAlertDialog = new AlertDialog.Builder(EditItemActivity.this);
        deleteTaskAlertDialog.setMessage("Are you sure you want to delete this task?");
        deleteTaskAlertDialog.setCancelable(true);

        deleteTaskAlertDialog.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    todoItemDAO.deleteItem(originalItem._id);
                    Intent data = new Intent();
                    data.putExtra(TodoAppConstants.GROUP_POSITION, groupPosition);
                    data.putExtra(TodoAppConstants.CHILD_POSITION, childPosition);
                    setResult(TodoAppConstants.RESULT_DELETED_TASK, data);
                    finish();
                    dialog.cancel();
                }
            });

        deleteTaskAlertDialog.setNegativeButton(
            "CANCEL",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                    dialog.cancel();
                }
            });
        AlertDialog deleteTaskAlert = deleteTaskAlertDialog.create();
        deleteTaskAlert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.delete_task:
                handleDeleteTask();
                return true;
            case R.id.save_action:
                onSaveItem();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }



}
