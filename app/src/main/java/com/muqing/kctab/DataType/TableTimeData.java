package com.muqing.kctab.DataType;

import com.muqing.kctab.MainActivity;

public class TableTimeData {
    public String title;
    public String starttime;
    public String endtime;

    public TableTimeData(String title, String starttime, String endtime) {
        this.title = title;
        this.starttime = starttime;
        this.endtime = endtime;
    }
    public static TableTimeData[] tableTimeData = new TableTimeData[]{
            new TableTimeData("1", "08:20", "09:05"),
            new TableTimeData("2", "09:15", "10:00"),
            new TableTimeData("3", "10:20", "11:05"),
            new TableTimeData("4", "11:15", "12:00"),
            new TableTimeData("5", "13:30", "14:15"),
            new TableTimeData("6", "14:25", "15:10"),
            new TableTimeData("7", "15:30", "16:15"),
            new TableTimeData("8", "16:25", "17:10"),
            new TableTimeData("9", "18:30", "19:15"),
            new TableTimeData("10", "19:25", "20:10")
    };
    public static final MainActivity.ScheduleItem[] schedule = {
            new MainActivity.ScheduleItem("1.2", "08:20-09:05", "09:15-10:00"),
            new MainActivity.ScheduleItem("3.4", "10:20-11:05", "11:15-12:00"),
            new MainActivity.ScheduleItem("5.6", "13:30-14:15", "14:25-15:10"),
            new MainActivity.ScheduleItem("7.8", "15:30-16:15", "16:25-17:10"),
            new MainActivity.ScheduleItem("9.10", "18:30-19:15", "19:25-20:10")
    };
    //    public static final MainActivity.ScheduleItem[] schedule = {new MainActivity.ScheduleItem("1.2", "08:20-09:05", "09:15-10:00"),
//            new MainActivity.ScheduleItem("3.4", "10:10-11:40", "10:30-12:00"),
//            new MainActivity.ScheduleItem("5.6", "13:30-14:15", "14:25-15:10"),
//            new MainActivity.ScheduleItem("7.8", "15:20-16:05", "16:15-17:00"),
//            new MainActivity.ScheduleItem("9.10", "18:30-19:15", "19:25-20:10")};
//
}
