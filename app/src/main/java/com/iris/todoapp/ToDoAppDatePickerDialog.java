package com.iris.todoapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by iris on 6/19/16.
 */
public class ToDoAppDatePickerDialog extends DatePickerDialog {

        public ToDoAppDatePickerDialog(Context context, OnDateSetListener callBack,
            int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);

            super.setCancelable(true);
            super.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        }

        public void setTitle(CharSequence title) {
            super.setTitle("Set a due date");
        }

}



