package com.example.eventapp.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListHeightAdapter {
    public static void setListHeightBasedOnChildren(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null){
            return;
        }
        int totalHeight = 0;
        int len = listAdapter.getCount();
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (len>=4){
            for (int i=0;i<3; i++){
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight+=listItem.getMeasuredHeight();
            }
            params.height = totalHeight+(listView.getDividerHeight()*2);
        } else {
            for (int i=0;i<len; i++){
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight+=listItem.getMeasuredHeight();
            }
            params.height = totalHeight+(listView.getDividerHeight()*(len-1));
        }

//        params.height = totalHeight+(listView.getDividerHeight()*(len-1));
        listView.setLayoutParams(params);
    }
}
