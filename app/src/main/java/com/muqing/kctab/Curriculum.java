package com.muqing.kctab;

import android.text.TextUtils;

import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

public class Curriculum {
    @SerializedName("Msg")
    public String msg;

    @SerializedName("code")
    public String code;

    @SerializedName("data")
    public List<DataItem> data;

    @SerializedName("needClassName")
    public String needClassName;

    @SerializedName("needClassRoomNub")
    public String needClassRoomNub;

    public static class DateInfo {
        @SerializedName("xqmc")
        public String xqmc;

        @SerializedName("mxrq")
        public String mxrq;

        @SerializedName("zc")
        public String zc;

        @SerializedName("xqid")
        public String xqid;

        @SerializedName("rq")
        public String rq;
    }

    public static class Course {
        @SerializedName("classWeek")
        public String classWeek;

        @SerializedName("teacherName")
        public String teacherName;

        @SerializedName("weekNoteDetail")
        public String weekNoteDetail;

        @SerializedName("buttonCode")
        public String buttonCode;

        @SerializedName("xkrs")
        public int xkrs;

        @SerializedName("ktmc")
        public String ktmc;

        @SerializedName("classTime")
        public String classTime;

        @SerializedName("classroomNub")
        public String classroomNub;

        @SerializedName("jx0408id")
        public String jx0408Id;

        @SerializedName("buildingName")
        public String buildingName;

        @SerializedName("courseName")
        public String courseName;

        @SerializedName("isRepeatCode")
        public String isRepeatCode;

        @SerializedName("jx0404id")
        public String jx0404Id;

        @SerializedName("weekDay")
        public int weekDay;

        @SerializedName("classroomName")
        public String classroomName;

        public String getClassroomName() {
            if (TextUtils.isEmpty(classroomName)) {
                classroomName = "网课";
            }
            return classroomName;
        }

        @SerializedName("khfs")
        public String khfs;

        @SerializedName("startTime")
        public String startTime;
        public String Time;
        public String Zhou;


        @SerializedName("endTIme")
        public String endTime;

        @SerializedName("location")
        public String location;

        @SerializedName("fzmc")
        public String fzmc;

        @SerializedName("classWeekDetails")
        public String classWeekDetails;

        @SerializedName("coursesNote")
        public int coursesNote;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Course)) return false;
            Course course = (Course) o;
            return xkrs == course.xkrs && weekDay == course.weekDay && coursesNote == course.coursesNote && Objects.equals(classWeek, course.classWeek) && Objects.equals(teacherName, course.teacherName) && Objects.equals(weekNoteDetail, course.weekNoteDetail) && Objects.equals(buttonCode, course.buttonCode) && Objects.equals(ktmc, course.ktmc) && Objects.equals(classTime, course.classTime) && Objects.equals(classroomNub, course.classroomNub) && Objects.equals(jx0408Id, course.jx0408Id) && Objects.equals(buildingName, course.buildingName) && Objects.equals(courseName, course.courseName) && Objects.equals(isRepeatCode, course.isRepeatCode) && Objects.equals(jx0404Id, course.jx0404Id) && Objects.equals(getClassroomName(), course.getClassroomName()) && Objects.equals(khfs, course.khfs) && Objects.equals(startTime, course.startTime) && Objects.equals(Time, course.Time) && Objects.equals(Zhou, course.Zhou) && Objects.equals(endTime, course.endTime) && Objects.equals(location, course.location) && Objects.equals(fzmc, course.fzmc) && Objects.equals(classWeekDetails, course.classWeekDetails);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classWeek, teacherName, weekNoteDetail, buttonCode, xkrs, ktmc, classTime, classroomNub, jx0408Id, buildingName, courseName, isRepeatCode, jx0404Id, weekDay, getClassroomName(), khfs, startTime, Time, Zhou, endTime, location, fzmc, classWeekDetails, coursesNote);
        }
    }

    public static class Node {
        @SerializedName("nodeName")
        public String nodeName;

        @SerializedName("nodeNumber")
        public String nodeNumber;
    }

    public static class TopInfo {
        @SerializedName("semesterId")
        public String semesterId;

        @SerializedName("week")
        public String week;

        @SerializedName("today")
        public String today;

        @SerializedName("weekday")
        public String weekday;

        @SerializedName("maxWeek")
        public String maxWeek;
    }

    public static class DataItem {
        @SerializedName("date")
        public List<DateInfo> date;

        @SerializedName("courses")
        public List<Course> courses;

        @SerializedName("nodesLst")
        public List<Node> nodesLst;

        @SerializedName("item")
        public List<List<List<Course>>> item;

        @SerializedName("week")
        public int week;

        @SerializedName("nodes")
        public Nodes nodes;

        @SerializedName("weekday")
        public String weekday;

        @SerializedName("bz")
        public String bz;

        @SerializedName("topInfo")
        public List<TopInfo> topInfo;
    }

    public static class Nodes {
        @SerializedName("sw")
        public List<String> sw;

        @SerializedName("ws")
        public List<String> ws;

        @SerializedName("zw")
        public List<String> zw;

        @SerializedName("xw")
        public List<String> xw;
    }
}