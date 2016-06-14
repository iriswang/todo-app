package com.iris.todoapp;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.iris.todoapp.TodoItem.Status;


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
        } else {
            txtListChildPriority.setText("");

        }

        return convertView;
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

}