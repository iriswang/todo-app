package com.iris.todoapp.views.adapters;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.iris.todoapp.R;
import com.iris.todoapp.models.TodoItem;
import com.iris.todoapp.models.TodoItem.Priority;
import com.iris.todoapp.models.TodoItem.Status;


public class TodoItemExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeaders;
    private HashMap<String, List<TodoItem>> _listDataChildren;

    public TodoItemExpandableListAdapter(Context context, List<String> listDataHeader,
        HashMap<String, List<TodoItem>> listChildData) {
        this._context = context;
        this._listDataHeaders = listDataHeader;
        this._listDataChildren = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChildren.get(this._listDataHeaders.get(groupPosition))
                                  .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
        boolean isLastChild, View convertView, ViewGroup parent) {

        final TodoItem childItem = (TodoItem) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.todo_list_item, null);
        }

        TextView txtListChildName = (TextView) convertView
            .findViewById(R.id.tvItemName);
        txtListChildName.setText(childItem.title);

        TextView txtListChildPriority = (TextView) convertView
            .findViewById(R.id.tvItemPriority);

        if (childItem.status == Status.TODO) {
            txtListChildPriority.setText(childItem.priority.toString());
            setPriorityViewColor(txtListChildPriority, childItem);
            txtListChildName.setTextColor(
                this._context.getResources().getColor(R.color.colorBlack));
        } else {
            txtListChildName.setTextColor(
                    this._context.getResources().getColor(R.color.colorCompletedText));
            txtListChildPriority.setText("");
        }

        return convertView;
    }

    private void setPriorityViewColor(TextView tv, TodoItem item) {
        if (item.priority == Priority.HIGH) {
            tv.setTextColor(
                this._context.getResources().getColor(R.color.colorStatusHighPri));
        } else if (item.priority == Priority.MEDIUM) {
            tv.setTextColor(
                this._context.getResources().getColor(R.color.colorStatusMedPri));
        } else {
            tv.setTextColor(
                this._context.getResources().getColor(R.color.colorStatusLowPri));
        }

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChildren.get(this._listDataHeaders.get(groupPosition))
                                  .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeaders.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeaders.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
        View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.todo_item_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
            .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetChanged() {
        for (Map.Entry<String, List<TodoItem>> entry: this._listDataChildren.entrySet()) {
            Collections.sort(entry.getValue(), new Comparator<TodoItem>() {
                    @Override
                    public int compare(TodoItem lhs, TodoItem rhs) {
                        return Priority.sort(lhs.priority, rhs.priority);
                    }
                });
        }

        super.notifyDataSetChanged();
    }


}