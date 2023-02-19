package com.example.eventapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

public class NewActivity extends AppCompatActivity {
    TextView tvDate;
    EditText etName,etDate,etTime,etRepeat,etNote;
    Button btnSave,btnClear,btnCalender,btnAlarm;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        // hide system title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null){
            actionBar.hide();
        }
        tvDate = findViewById(R.id.tv_date3);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        tvDate.setText(sdf.format(new Date(System.currentTimeMillis())));

        etName = findViewById(R.id.et_name1);
        etDate = findViewById(R.id.et_date1);
        etTime = findViewById(R.id.et_time1);
        etRepeat = findViewById(R.id.et_repeat1);
        etNote = findViewById(R.id.et_note1);

        btnCalender = findViewById(R.id.btn_calender2);
        btnAlarm = findViewById(R.id.btn_alarm1);
        btnSave = findViewById(R.id.btn_save);
        btnClear = findViewById(R.id.btn_clear1);
        dbHelper = new DBHelper(this);

        btnCalender.setOnClickListener(v -> showDateDialog().show());
        btnAlarm.setOnClickListener(v -> showTimeDialog().show());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String date = etDate.getText().toString();
            if (name.equals("") || date.equals("")){
                Toast.makeText(NewActivity.this,
                        "name and date can't be empty!", Toast.LENGTH_SHORT).show();

            } else {
                String repeat = etRepeat.getText().toString();
                if (repeat.equals("")){
                    repeat = "0";
                }
                Event e1 = new Event(name, date, etTime.getText().toString(),
                        Integer.parseInt(repeat), etNote.getText().toString());
                // query whether exist
                Event e2 = dbHelper.queryEvent(e1);
                if (e1.toString().equals(e2.toString())){
                    Toast.makeText(NewActivity.this, "you have added it",Toast.LENGTH_SHORT).show();
                } else {
                    String tag = dbHelper.addEvent(e1);
                    Toast.makeText(NewActivity.this, tag, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClear.setOnClickListener(v -> clearAll());
    }

    private DatePickerDialog showDateDialog(){
        Calendar calendar = Calendar.getInstance();
        return new DatePickerDialog(NewActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        return new TimePickerDialog(NewActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

    private void clearAll(){
        etName.setText("");
        etDate.setText("");
        etTime.setText("");
        etRepeat.setText("");
        etNote.setText("");
    }

}