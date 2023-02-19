package com.example.eventapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventapp.adapter.DayAdapter;
import com.example.eventapp.model.Day;
import com.example.eventapp.model.DayInfo;
import com.example.eventapp.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CalenderActivity extends AppCompatActivity {
    TextView tvDate,tvChosenDate,tvChosenDate2;
    Button btnPre,btnNext;
    GridView gridView;
    ListView listView;
    private List<DayInfo> dayInfoList;
    private DBHelper dbHelper;
    private DayAdapter dayAdapter;
    private Calendar calendar;
    private SimpleAdapter adapter;
    private List<HashMap<String, Object>> hashMapList;
    private String dateChosen;

    @Override
    protected void onRestart() {
        super.onRestart();
        showList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
//        hide system bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null){
            actionBar.hide();
        }

        tvDate = findViewById(R.id.tv_date2);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        tvDate.setText(sdf.format(new Date(System.currentTimeMillis())));

        tvChosenDate = findViewById(R.id.tv_selectDate);
        btnPre = findViewById(R.id.btn_pre);
        btnNext = findViewById(R.id.btn_next);
        gridView = findViewById(R.id.gv1);
        tvChosenDate2 = findViewById(R.id.tv_chosenDate);
        listView = findViewById(R.id.list_4);

        dbHelper = new DBHelper(CalenderActivity.this);
        dayInfoList = new ArrayList<>();
        calendar = Calendar.getInstance();
        dayAdapter = new DayAdapter(dayInfoList, this);

//        initAdapter();

        // 显示到 GridView
        gridView.setAdapter(dayAdapter);
        setCurrentDate(calendar);  // current date
        // 展示当前年月
        tvChosenDate.setText(setBoundary(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1));  // current month
        updateAdapter(calendar, dayInfoList, dayAdapter);  // month days

        // 按钮点击事件
        btnPre.setOnClickListener(view -> {
            tvChosenDate.setText(setBoundary(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1 - 1));  // select month
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);  // 上月
            updateAdapter(calendar, dayInfoList, dayAdapter);
            tvChosenDate2.setText("");
        });
        btnNext.setOnClickListener(view -> {
            tvChosenDate.setText(setBoundary(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1 + 1));  // select month
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1); // 下月
            updateAdapter(calendar, dayInfoList, dayAdapter);
            tvChosenDate2.setText("");
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DayInfo dayInfo = (DayInfo) parent.getItemAtPosition(position);
                Day day = dayInfo.getDay();
                CharSequence cs = day.getYear() + "年" + day.getMonth() +"月"+day.getDay()+"日";
                tvChosenDate2.setText(cs);
                dateChosen = day.getYear()+"-"+day.getMonth()+"-"+day.getDay();
                showList();
            }
        });

    }

    // 获取显示月份的天
    private void updateAdapter(Calendar calendar, List<DayInfo> dayInfoList, DayAdapter dayAdapter){
        dayInfoList.clear();
        // get the index of Monday in this month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // from Sunday start, so Monday will -1
        int weekIndex = calendar.get(Calendar.DAY_OF_WEEK) -1;

        // set pre month
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
        int preMonthDays = getMonthDays(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1);

        // get the last days in previous month
        for (int i=0; i<weekIndex; i++){
            Day day = new Day(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1,
                    preMonthDays - weekIndex + i + 1, false, false);
            DayInfo dayInfo = new DayInfo();
            dayInfo.setDay(day);
            List<String> nameList = new ArrayList<>();
//            nameList.add("none");
            dayInfo.setNameList(nameList);
            dayInfoList.add(dayInfo);
        }

        // get current month days
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        int curMonthDays = getMonthDays(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1);

        Calendar calendar1 = Calendar.getInstance();  // get current date
        for (int i = 0; i < curMonthDays; i++){
            String curDate = calendar.get(Calendar.YEAR)+"-"
                    +(calendar.get(Calendar.MONTH)+1)+"-"
                    +calendar1.get(Calendar.DAY_OF_MONTH);
            String selectDate = calendar.get(Calendar.YEAR)+"-"
                    +(calendar.get(Calendar.MONTH)+1)+"-"
                    + (i + 1);
            Day day = new Day(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    i + 1, true, curDate.equals(selectDate));
            DayInfo dayInfo = new DayInfo();
            dayInfo.setDay(day);
            List<String> nameList = new ArrayList<>();
            String value =  calendar.get(Calendar.YEAR) + "-" +
                    (calendar.get(Calendar.MONTH) + 1) + "-" + (i + 1);
            List<Event> eventList = dbHelper.queryEvents("date", value);
            for(Event e: eventList){
                nameList.add(e.getName());
            }
            dayInfo.setNameList(nameList);
            dayInfoList.add(dayInfo);
        }

        // get the previous days in next month
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        for (int i = 0; i < 7 - weekIndex; i++){
            Day day = new Day(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    i + 1, false, false);
            DayInfo dayInfo = new DayInfo();
            dayInfo.setDay(day);
            List<String> nameList = new ArrayList<>();
//            nameList.add("none");
            dayInfo.setNameList(nameList);
            dayInfoList.add(dayInfo);
        }
        dayAdapter.notifyDataSetChanged();
        // back to current date
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
    }

    private void setCurrentDate(Calendar calendar){
        CharSequence charSequence = calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.DAY_OF_MONTH);
        tvChosenDate.setText(charSequence);
    }
    // 设置边界值
    private CharSequence setBoundary(int year,int month){
        switch (month){
            case 0:
                month = 12;
                year -= 1;
                break;
            case 13:
                month = 1;
                year += 1;
                break;
            default:
                break;
        }
        return year + "年" + month + "月";
    }
    // 判断是否为闰年
    private boolean isLeap(int year){
        return year % 4 == 0;
    }
    // 根据年月设置天数
    private int getMonthDays(int year, int month){
        switch (month) {
            case 2:
                return isLeap(year) ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    // get event in chosen date
    private void showList(){
        List<Event> events = dbHelper.queryEvents("date", dateChosen);
        hashMapList = new ArrayList<>();
        for(Event e:events){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", e.getName());
            hashMap.put("date", e.getDate());
            hashMap.put("time", e.getTime());
            hashMap.put("repeat", String.valueOf(e.getRepeat()));
            hashMap.put("note", e.getNote());
            hashMapList.add(hashMap);
        }
        adapter = new SimpleAdapter(CalenderActivity.this, hashMapList,
                R.layout.list_item4,
                new String[]{"name", "date", "time", "repeat"},
                new int[]{R.id.tv_eventName, R.id.tv_eventDate,
                        R.id.tv_eventTime, R.id.tv_eventRepeat});
        listView.setAdapter(adapter);
    }
}