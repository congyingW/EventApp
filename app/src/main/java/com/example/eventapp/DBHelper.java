package com.example.eventapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.eventapp.model.Event;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "events.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table event(" +
                "name varchar(50) not null, " +
                "date text not null, " +
                "time text, " +
                "repeat integer, " +
                "note varchar(300));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // operation
//    1. insert event
    public String addEvent(Event event){
        ContentValues cv = new ContentValues();
        cv.put("name", event.getName());
        cv.put("date", event.getDate());
        cv.put("time", event.getTime());
        cv.put("repeat", event.getRepeat());
        cv.put("note",event.getNote());
        // 进行写入
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        long insert = sqlDB.insert("event", null, cv);
        sqlDB.close();
        if (insert == -1){
            return "fail";
        }
        return "success";
    }
//    2. delete one event according to all
    public String deleteEvent(Event event){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int delete = sqLiteDatabase.delete("event",
                "name=? and date=? and time=? and repeat=? and note=?",
                new String[]{event.getName(), event.getDate(), event.getTime(),
                        String.valueOf(event.getRepeat()), event.getNote()});
        sqLiteDatabase.close();
        if (delete == 0){
            return "fail";
        }
        return "success";
    }
//    3. modify event
//    public String upgradeEvent(Event event){
//        ContentValues cv = new ContentValues();
//        cv.put("name", event.getName());
//        cv.put("date", event.getDate());
//        cv.put("time", event.getTime());
//        cv.put("repeat", event.getRepeat());
//        cv.put("note",event.getNote());
//        // 进行写入
//        SQLiteDatabase sqlDB = this.getWritableDatabase();
//        int upgrade = sqlDB.update("event", cv,
//                "name=? and date=?",
//                new String[]{event.getName(), event.getDate()});
//        sqlDB.close();
//        if (upgrade == 0){
//            return "fail";
//        }
//        return "success";
//    }

//    4. query the only one event according to all
    public Event queryEvent(Event e){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String sql = "select * from event where name=? and date=? and time=? and repeat=? and note=?";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{e.getName(), e.getDate(),
                e.getTime(), String.valueOf(e.getRepeat()), e.getNote()});
        int nameI = cursor.getColumnIndex("name");
        int dateI = cursor.getColumnIndex("date");
        int timeI = cursor.getColumnIndex("time");
        int repeatI = cursor.getColumnIndex("repeat");
        int noteI = cursor.getColumnIndex("note");
        if (cursor.getCount() == 0){
            Event event = new Event("name", "date", "time", 0, "note");
            cursor.close();
            sqLiteDatabase.close();
            return event;
        } else {
            cursor.moveToNext();
            Event event = new Event(cursor.getString(nameI), cursor.getString(dateI),
                    cursor.getString(timeI), cursor.getInt(repeatI), cursor.getString(noteI));
            cursor.close();
            sqLiteDatabase.close();
            return event;
        }

    }
//    5. query events according to name or date
    public List<Event> queryEvents(String key, String value){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String sqlSelect = "";
        if (key.equals("name")){
            sqlSelect="select * from event where name=? order by date(date) desc";
        } else if (key.equals("date")){
            sqlSelect="select * from event where date=? order by date(date) desc";
        }
        Cursor cursor = sqLiteDatabase.rawQuery(sqlSelect, new String[]{value});
        List<Event> eventList = getEvents(cursor);
        cursor.close();
        sqLiteDatabase.close();
        return eventList;
    }
//    6. query all events
    public List<Event> getAll(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from event order by date(date) desc", null);
        List<Event> eventList = getEvents(cursor);
        cursor.close();
        sqLiteDatabase.close();
        return eventList;
    }

    private List<Event> getEvents(Cursor cursor) {
        List<Event> eventList = new ArrayList<>();
        int name = cursor.getColumnIndex("name");
        int date = cursor.getColumnIndex("date");
        int time = cursor.getColumnIndex("time");
        int repeat = cursor.getColumnIndex("repeat");
        int note = cursor.getColumnIndex("note");
        while (cursor.moveToNext()){
            Event event = new Event(cursor.getString(name), cursor.getString(date),
                    cursor.getString(time), cursor.getInt(repeat), cursor.getString(note));
            eventList.add(event);
        }
        return eventList;
    }
}
