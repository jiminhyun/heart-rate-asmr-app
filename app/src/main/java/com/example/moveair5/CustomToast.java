package com.example.moveair5;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

    public static void showToast(Context context, String message) {

        Toast toast = new Toast(context);

        View view = LayoutInflater.from(context)
                .inflate(R.layout.toast_layout, null);

        TextView custom_toast_message = view.findViewById(R.id.toastMessage);
        custom_toast_message.setText(message);

        toast.setGravity(Gravity.TOP, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();

    }
}
