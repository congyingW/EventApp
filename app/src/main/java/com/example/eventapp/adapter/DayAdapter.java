package com.example.eventapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eventapp.R;
import com.example.eventapp.model.Day;
import com.example.eventapp.model.DayInfo;

import java.util.List;

public class DayAdapter extends BaseAdapter {
    private List<DayInfo> list;
    private Context context;

    public DayAdapter(List<DayInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = View.inflate(context, R.layout.grid_item, null);
        }
        TextView tvDay = convertView.findViewById(R.id.tv_day);
        ListView listView = convertView.findViewById(R.id.list_3);
        DayInfo dayInfo = (DayInfo) getItem(position);
        Day day = dayInfo.getDay();
        List<String> nameList = dayInfo.getNameList();
//        设置几号
        CharSequence csDay = String.valueOf(day.getDay());
        tvDay.setText(csDay);
        // 设置当天当月，其它天的形式
        if (day.isCurDay()){
            tvDay.setBackgroundColor(Color.parseColor("#E64100"));
            tvDay.setTextColor(Color.WHITE);
        } else if (day.isCurMonth()) {
            tvDay.setTextColor(Color.BLACK);
            tvDay.setBackgroundColor(Color.parseColor("#00000000"));
        } else {
            tvDay.setTextColor(Color.parseColor("#bbbb99"));
        }
        if (nameList.size()>3){
            nameList.set(2, "...");
        }
//        设置listview显示内容
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.list_item3,
                R.id.tv_name, nameList);
        listView.setAdapter(adapter);
        ListHeightAdapter.setListHeightBasedOnChildren(listView);

        return convertView;
    }
}
