package com.example.eventapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventapp.model.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {
    TextView tvDate;
    EditText etName,etDate,etTime,etRepeat,etNote;
    Button btnUpgrade,btnCalender,btnAlarm;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        // hide system title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null){
            actionBar.hide();
        }
        tvDate = findViewById(R.id.tv_date4);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        tvDate.setText(sdf.format(new Date(System.currentTimeMillis())));

        etName = findViewById(R.id.et_name2);
        etDate = findViewById(R.id.et_date2);
        etTime = findViewById(R.id.et_time2);
        etRepeat = findViewById(R.id.et_repeat2);
        etNote = findViewById(R.id.et_note2);

        btnCalender = findViewById(R.id.btn_calender3);
        btnAlarm = findViewById(R.id.btn_alarm2);
        btnUpgrade = findViewById(R.id.btn_upgrade);
        dbHelper = new DBHelper(EditActivity.this);

        // save name and date value, and query event
        Intent intent = getIntent();
        String name2 = intent.getStringExtra("name");
        String date2 = intent.getStringExtra("date");
        String time2 = intent.getStringExtra("time");
        String repeat2 = intent.getStringExtra("repeat");
        String note2 = intent.getStringExtra("note");
        Event e1 = new Event(name2,date2, time2, Integer.parseInt(repeat2), note2);
        // show primitive event
        etName.setText(name2);
        etDate.setText(date2);
        etTime.setText(time2);
        etRepeat.setText(repeat2);
        etNote.setText(note2);

        btnCalender.setOnClickListener(v -> showDateDialog().show());
        btnAlarm.setOnClickListener(v -> showTimeDialog().show());

        btnUpgrade.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String date = etDate.getText().toString();
            String time = etTime.getText().toString();
            String repeat = etRepeat.getText().toString();
            String note = etNote.getText().toString();
            if (repeat.equals("")){
                repeat = "0";
            }

            if (name.equals("") || date.equals("")){
                Toast.makeText(EditActivity.this,
                        "name and date can't be empty!", Toast.LENGTH_SHORT).show();
            } else {
                Event e = new Event(name, date, etTime.getText().toString(),
                        Integer.parseInt(repeat), etNote.getText().toString());
                // query whether exist
                Event e2 = dbHelper.queryEvent(e);
                if (e.toString().equals(e2.toString())){
                    Toast.makeText(EditActivity.this,
                            "you have added it", Toast.LENGTH_SHORT).show();
                } else {
                    // change name or date, then delete primitive event save new event
                    dbHelper.deleteEvent(e1);
                    String tag = dbHelper.addEvent(e);
                    Toast.makeText(EditActivity.this, tag, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private DatePickerDialog showDateDialog(){
        Calendar calendar = Calendar.getInstance();
        return new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                CharSequence csDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                etDate.setText(csDate);
            }
        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private TimePickerDialog showTimeDialog(){
        Calendar calendar = Calendar.getInstance();
        return new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String h = String.valueOf(hourOfDay);
                String m = String.valueOf(minute);
                if (hourOfDay<10){
                    h = "0" + h;
                }
                if(minute < 10){
                    m = "0" + m;
                }
                CharSequence csTime  = h + ":" + m;
                etTime.setText(csTime);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
    }

}