package com.iris.todoapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.iris.todoapp.TodoItem.Status;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by iris on 3/26/16.
 */
public class TodoItemAdapter extends ArrayAdapter<TodoItem> {

    private static class ViewHolder {

        TextView title;
    }

    public TodoItemAdapter(Context context, ArrayList<TodoItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TodoItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;

        if (convertView == null || convertView.getTag() == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.todo_list_item, parent, false);
            if (item.status == Status.DONE) {
                viewHolder.title = (TextView) convertView.findViewById(R.id.tvEditItem);
                viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                viewHolder.title = (TextView) convertView.findViewById(R.id.tvEditItem);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(item.title);
        return convertView;
    }
}
