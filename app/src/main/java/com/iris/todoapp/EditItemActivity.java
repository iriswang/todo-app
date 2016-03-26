package com.iris.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    int position;
    TodoItem item;
    EditText editItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        editItem = (EditText) findViewById(R.id.etEditItem);
        position = getIntent().getIntExtra("list_position", -1);
        item = (TodoItem) getIntent().getSerializableExtra("item");
        editItem.setText(item.title);
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
