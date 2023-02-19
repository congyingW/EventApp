package com.example.eventapp.model;

public class Day {
    private int year;
    private int month;
    private int day;
    private boolean isCurMonth;
    private boolean isCurDay;

    public Day(int year, int month, int day, boolean isCurMonth, boolean isCurDay) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.isCurMonth = isCurMonth;
        this.isCurDay = isCurDay;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isCurMonth() {
        return isCurMonth;
    }

    public void setCurMonth(boolean curMonth) {
        isCurMonth = curMonth;
    }

    public boolean isCurDay() {
        return isCurDay;
    }

    public void setCurDay(boolean curDay) {
        isCurDay = curDay;
    }
}
