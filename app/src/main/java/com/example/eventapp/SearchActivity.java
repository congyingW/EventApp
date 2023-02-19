package com.example.eventapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventapp.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    TextView tvDate,tvResult;
    EditText etName;
    Button btnClear,btnCal,btnSearch;
    ListView listView;
    private String key;
    private String value;
    private DBHelper dbHelper;
    private List<HashMap<String, Object>> hashMapList;
    private List<Event> eventList;
    private SimpleAdapter adapter;

    @Override
    protected void onRestart() {
        super.onRestart();
        listAdapter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // hide system title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null){
            actionBar.hide();
        }
        tvDate = findViewById(R.id.tv_date5);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        tvDate.setText(sdf.format(new Date(System.currentTimeMillis())));

        etName = findViewById(R.id.et_name3);
        btnClear = findViewById(R.id.btn_clear2);
        btnCal = findViewById(R.id.btn_calender4);
        btnSearch = findViewById(R.id.btn_search2);
        tvResult = findViewById(R.id.tv_result);
        listView = findViewById(R.id.list_2);

        dbHelper = new DBHelper(SearchActivity.this);

        btnClear.setOnClickListener(v -> etName.setText(""));
        key = "name";
        btnCal.setOnClickListener(v -> {
            key = "date";
            showDateDialog().show();
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                value = etName.getText().toString();
                if (value.equals("")){
                    Toast.makeText(SearchActivity.this,
                            "input name or date", Toast.LENGTH_SHORT).show();
                } else {
                    // get search result
                    listAdapter();
                    if (eventList.size()==0){
                        tvResult.setText("not find");
                        hashMapList.clear();
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    } else {
                        tvResult.setText("search result");
                        listAdapter();
                    }
                }
            }
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
                Intent intent = new Intent(SearchActivity.this, EditActivity.class);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchActivity.this);
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
                        Toast.makeText(SearchActivity.this, tag, Toast.LENGTH_SHORT).show();
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
    }

    private DatePickerDialog showDateDialog(){
        Calendar calendar = Calendar.getInstance();
        return new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                CharSequence csDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                etName.setText(csDate);
            }
        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void listAdapter(){
        eventList = dbHelper.queryEvents(key, value);
        hashMapList = new ArrayList<>();
        for(Event e:eventList){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", e.getName());
            hashMap.put("date", e.getDate());
            hashMap.put("time", e.getTime());
            hashMap.put("repeat", String.valueOf(e.getRepeat()));
            hashMap.put("note", e.getNote());
            hashMapList.add(hashMap);
        }
        adapter = new SimpleAdapter(SearchActivity.this, hashMapList, R.layout.list_item2,
                new String[]{"name", "date", "time", "repeat"},
                new int[]{R.id.tv_eventName1, R.id.tv_eventDate1,
                        R.id.tv_eventTime1, R.id.tv_eventRepeat1});
        listView.setAdapter(adapter);
    }

}