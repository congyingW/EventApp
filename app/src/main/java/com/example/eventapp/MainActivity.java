package com.example.eventapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventapp.adapter.RemindActionService;
import com.example.eventapp.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView tvTime,tvWeek,tvDate;
    Button btnCalender, btnNew, btnSearch;
    ListView listView;
    private DBHelper dbHelper;
    private SimpleAdapter adapter;
    private List<HashMap<String, Object>> hashMapList;
    @Override
    protected void onRestart() {
        super.onRestart();
        // 再次进入主页面时重新加载数据
        showAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // hide system title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null){
            actionBar.hide();
        }
        // show title date
        tvTime = findViewById(R.id.tv_time1);
        tvWeek = findViewById(R.id.tv_week1);
        tvDate = findViewById(R.id.tv_date1);
        new TimeThread(tvTime, tvWeek, tvDate).start();

        btnCalender = findViewById(R.id.btn_calender1);
        btnNew = findViewById(R.id.btn_new1);
        btnSearch = findViewById(R.id.btn_search1);
        listView = findViewById(R.id.list_1);

        showAll();
        btnCalender.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalenderActivity.class);
            startActivity(intent);
        });
        btnNew.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewActivity.class);
            startActivity(intent);
        });
        btnSearch.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get click item
                HashMap<String, Object> hashMap = (HashMap<String, Object>) parent.getItemAtPosition(position);
                String name = (String) hashMap.get("name");
                String date = (String) hashMap.get("date");
                String time = (String) hashMap.get("time");
                String repeat = (String) hashMap.get("repeat");
                String note = (String) hashMap.get("note");
                assert repeat != null;
                // jump to edit and send the value of name and date to edit activity
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("repeat", repeat);
                intent.putExtra("note", note);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Delete Confirmation");
                alertDialog.setMessage("Are you sure to delete it?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, Object> hashMap = (HashMap<String, Object>) parent.getItemAtPosition(position);
                        String name = (String) hashMap.get("name");
                        String date = (String) hashMap.get("date");
                        String time = (String) hashMap.get("time");
                        String repeat = (String) hashMap.get("repeat");
                        String note = (String) hashMap.get("note");
                        assert repeat != null;
                        Event e2= new Event(name, date, time, Integer.parseInt(repeat), note);
                        String tag = dbHelper.deleteEvent(e2);
                        hashMapList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast toast = Toast.makeText(MainActivity.this, tag, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                });

                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.create().show();
                return true;
            }
        });
        createNotificationChannel();
        try {
            sendToday();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void showAll(){
        dbHelper = new DBHelper(this);
        hashMapList = new ArrayList<>();
        List<Event> eventList = dbHelper.getAll();
        if (eventList.size()>0){
            for(Event e:eventList){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", e.getName());
                hashMap.put("date", e.getDate());
                hashMap.put("time", e.getTime());
                hashMap.put("repeat", String.valueOf(e.getRepeat()));
                hashMap.put("note", e.getNote());
                hashMapList.add(hashMap);
            }
            adapter = new SimpleAdapter(this, hashMapList, R.layout.list_item,
                    new String[]{"name", "date", "time", "repeat"},
                    new int[]{R.id.tv_eventName, R.id.tv_eventDate,
                            R.id.tv_eventTime, R.id.tv_eventRepeat});
            listView.setAdapter(adapter);
        }
    }

    private void sendToday() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        StringBuilder d2 = new StringBuilder(calendar.get(Calendar.YEAR) + "-" +
                (calendar.get(Calendar.MONTH) + 1) + "-" +
                calendar.get(Calendar.DAY_OF_MONTH));

        dbHelper = new DBHelper(this);
        List<Event> eventList = dbHelper.queryEvents("date", d2.toString());
        int num = eventList.size();
        if (num>0){
            for(Event e:eventList){
                if(!e.getTime().equals("")){
                    d2.append(" ").append(e.getTime());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                    Date d = sdf.parse(String.valueOf(d2));
                    assert d != null;
                    long dateMills = d.getTime();
                    Intent intent = new Intent(MainActivity.this, RemindActionService.class);
                    intent.setAction("notify");
                    intent.putExtra("name", e.getName());

                    PendingIntent pendingIntent;
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                        pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                                PendingIntent.FLAG_IMMUTABLE);
                    } else{
                        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                    }

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, dateMills, pendingIntent);
                }
            }
        }
}
    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("10", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}