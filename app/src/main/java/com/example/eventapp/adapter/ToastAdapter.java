package com.example.eventapp.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class ToastAdapter extends Toast {
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public ToastAdapter(Context context) {
        super(context);
    }


}
