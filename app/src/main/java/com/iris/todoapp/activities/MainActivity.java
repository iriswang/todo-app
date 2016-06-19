package com.iris.todoapp.activities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.google.common.collect.ImmutableMap;

import com.iris.todoapp.R;
import com.iris.todoapp.utils.TodoAppConstants;
import com.iris.todoapp.models.TodoItem;
import com.iris.todoapp.models.TodoItem.Priority;
import com.iris.todoapp.models.TodoItem.Status;
import com.iris.todoapp.models.TodoItemDatabaseDAO;
import com.iris.todoapp.models.TodoItemDatabaseHelper;
import com.iris.todoapp.views.adapters.TodoItemExpandableListAdapter;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String COMPLETED_ITEMS = "COMPLETED";
    private final String UNFINISHED_ITEMS = "TO DO";
    DrawerLayout _drawerLayout;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;


    private final Map<Status, String> statusToGroupNameMap = ImmutableMap.of(
        Status.DONE, COMPLETED_ITEMS,
        Status.TODO, UNFINISHED_ITEMS
    );

    TodoItemExpandableListAdapter todoItemListAdapter;
    ExpandableListView expListView;
    List<String> todoItemListDataHeaders;
    HashMap<String, List<TodoItem>> todoItemsListDataChildren;

    TodoItemDatabaseDAO todoItemDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TodoItemDatabaseHelper todoItemDBHelper = new TodoItemDatabaseHelper(this);
        this.todoItemDAO = new TodoItemDatabaseDAO(todoItemDBHelper.getWritableDatabase());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolBar();

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        expListView.setGroupIndicator(null);

        prepareListData();
        setUpExpListViewClickHandlers();
        todoItemListAdapter = new TodoItemExpandableListAdapter(this, todoItemListDataHeaders,
            todoItemsListDataChildren);

        expListView.setAdapter(todoItemListAdapter);
        expListView.expandGroup(0);
        expListView.expandGroup(1);
        expListView.setFocusable(false);

        _drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    return true;
                }
            });
    }

    private void prepareListData() {
        todoItemListDataHeaders = new ArrayList<String>();
        todoItemsListDataChildren = new HashMap<String, List<TodoItem>>();
        todoItemListDataHeaders.add(UNFINISHED_ITEMS);
        todoItemListDataHeaders.add(COMPLETED_ITEMS);
        List<TodoItem> sortedUnfinishedItems = todoItemDAO.getUnfinishedItems();
        Collections.sort(sortedUnfinishedItems, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem lhs, TodoItem rhs) {
                return Priority.sort(lhs.priority, rhs.priority);
            }
        });
        todoItemsListDataChildren.put(UNFINISHED_ITEMS, sortedUnfinishedItems);
        List<TodoItem> sortedCompletedItems = todoItemDAO.getCompletedItems();
        Collections.sort(sortedCompletedItems, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem lhs, TodoItem rhs) {
                return Priority.sort(lhs.priority, rhs.priority);
            }
        });
        todoItemsListDataChildren.put(COMPLETED_ITEMS, sortedCompletedItems);
    }

    private void setUpExpListViewClickHandlers() {

        expListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                long id) {
                return handleDelete(id);
            }
        });

        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                int groupPosition, int childPosition, long id) {
                launchEditItemView(groupPosition, childPosition);
                return true;
            }
        });

    }

    private boolean handleDelete(long id) {
        int itemType = ExpandableListView.getPackedPositionType(id);

        if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int childPosition = ExpandableListView.getPackedPositionChild(id);
            int groupPosition = ExpandableListView.getPackedPositionGroup(id);
            String groupName = todoItemListDataHeaders.get(groupPosition);
            TodoItem itemToDelete =
                todoItemsListDataChildren.get(groupName).get(childPosition);
            todoItemDAO.deleteItem(itemToDelete._id);
            todoItemsListDataChildren.get(groupName).remove(childPosition);
            todoItemListAdapter.notifyDataSetChanged();
            return true; //true if we consumed the click, false if not
        } else {
            return false;
        }

    }

    private void launchEditItemView(int groupPosition, int childPosition) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra(TodoAppConstants.GROUP_POSITION, groupPosition);
        i.putExtra(TodoAppConstants.CHILD_POSITION, childPosition);
        i.putExtra("item", fetchTodoItem(groupPosition, childPosition));
        i.putExtra("request_type", TodoAppConstants.EDIT_REQUEST_CODE);
        startActivityForResult(i, TodoAppConstants.EDIT_REQUEST_CODE);
    }

    private TodoItem fetchTodoItem(int groupPosition, int childPosition) {
        return todoItemsListDataChildren.get(
            todoItemListDataHeaders.get(groupPosition)).get(childPosition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TodoAppConstants.EDIT_REQUEST_CODE) {
            handleEditItemResult(data, resultCode);
        } else if (requestCode == TodoAppConstants.ADD_REQUEST_CODE) {
            handleAddItemResult(data, resultCode);
        }
    }

    private void handleEditItemResult(Intent data, int resultCode) {
        if (resultCode == RESULT_OK) {
            TodoItem newItem = (TodoItem) data.getSerializableExtra("new_item");
            int groupPosition = data.getExtras().getInt(TodoAppConstants.GROUP_POSITION);
            int childPosition = data.getExtras().getInt(TodoAppConstants.CHILD_POSITION);
            TodoItem oldItem = fetchTodoItem(groupPosition, childPosition);
            if (newItem != oldItem) {
                String newGroupName = statusToGroupNameMap.get(newItem.status);
                if (newItem.status != oldItem.status) {
                    String groupName = todoItemListDataHeaders.get(groupPosition);
                    todoItemsListDataChildren.get(groupName).remove(childPosition);
                    todoItemsListDataChildren.get(newGroupName).add(newItem);
                } else {
                    todoItemsListDataChildren.get(newGroupName).set(
                        childPosition, newItem);
                }
                todoItemListAdapter.notifyDataSetChanged();
           }
        } else if (resultCode == TodoAppConstants.RESULT_DELETED_TASK) {
            int groupPosition = data.getExtras().getInt(TodoAppConstants.GROUP_POSITION);
            String groupName = todoItemListDataHeaders.get(groupPosition);
            int childPosition = data.getExtras().getInt(TodoAppConstants.CHILD_POSITION);
            todoItemsListDataChildren.get(groupName).remove(childPosition);
            todoItemListAdapter.notifyDataSetChanged();
        }
    }

    private void handleAddItemResult(Intent data, int resultCode) {
        if (resultCode == RESULT_OK) {
            TodoItem newItem = (TodoItem) data.getSerializableExtra("new_item");
            todoItemsListDataChildren.get(UNFINISHED_ITEMS).add(newItem);
            todoItemListAdapter.notifyDataSetChanged();
        }
    }

    private void addNewTask() {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra("request_type", TodoAppConstants.ADD_REQUEST_CODE);
        startActivityForResult(i, TodoAppConstants.ADD_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_task:
                addNewTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
