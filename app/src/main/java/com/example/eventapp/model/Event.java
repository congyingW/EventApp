package com.example.eventapp.model;

public class Event {
    private String name;
    private String date;
    private String time;
    private int repeat;
    private String note;

    public Event(String name, String date, String time, int repeat, String note) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.repeat = repeat;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", repeat=" + repeat +
                ", note='" + note + '\'' +
                '}';
    }
}
