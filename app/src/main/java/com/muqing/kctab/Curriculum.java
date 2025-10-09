package com.muqing.kctab;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.muqing.gj;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.gson.GsonRuntime;

public class Curriculum {
    public List<Course> course;   // 课程列表
    public String nian;           // 学年标记
    public String startDate;      // 本学期开始日期（格式：yyyy-MM-dd）
    public Integer zhouInt;       // 本学期总周数（null 时默认 20）

    // 构造函数
    public Curriculum() {
        this.course = new ArrayList<>();
    }

    // 可选字段的构造函数（重载）
    public Curriculum(List<Course> course, String nian, String startDate, Integer zhouInt) {
        this.course = course;
        this.nian = nian;
        this.startDate = startDate;
        this.zhouInt = zhouInt;
    }

    private static final JmesPath<JsonElement> jmespath = new GsonRuntime();

    @NonNull
    public static Curriculum JieXi(String json, Curriculum curriculum) {
        if (curriculum == null) {
            curriculum = new Curriculum();
        }
        JsonElement jsonObj = JsonParser.parseString(json);
        Expression<JsonElement> compile = jmespath.compile("data[0].courses");
        JsonElement search = compile.search(jsonObj);
        if (!search.isJsonArray()) return curriculum;
        JsonArray courses = search.getAsJsonArray();
        for (JsonElement course : courses) {
            // 获取课程名称
            String courseName = getSearch(course, "courseName");
            Course co = curriculum.course.stream().filter(c -> Objects.equals(c.courseName, courseName))
                    .findFirst().orElse(new Course());
            co.courseName = courseName;
            co.teacherName = getSearch(course, "teacherName");
            co.ktmc = getSearch(course, "ktmc");
            String weekNoteDetail = getSearch(course, "weekNoteDetail");
            String classroomName = getSearch(course, "classroomName");
            String classweek = getSearch(course, "classWeek");
//                String classWeekDetails  = getSearch(course, "classWeekDetails");
            if (co.classWeekDetails == null) {
                co.classWeekDetails = new ArrayList<>();
            }
//            gj.sc(new Gson().toJson(co.classWeekDetails));
            classWeekDetails adata = co.classWeekDetails.stream().filter(a -> Objects.equals(a.weeks, classweek))
                    .findFirst()
                    .orElse(null);
            if (adata == null) {
                classWeekDetails cs = new classWeekDetails();
                Weekday weekday = new Weekday();
                weekday.jie = weekNoteDetail;
                weekday.classroomName = classroomName;
                cs.weeks = classweek;
                cs.weekdays = new ArrayList<>();
                cs.weekdays.add(weekday);
                co.classWeekDetails.add(cs);
            } else {
                boolean b = adata.weekdays.stream().anyMatch(a -> a.jie.equals(weekNoteDetail));
                if (!b) {
                    Weekday weekday = new Weekday();
                    weekday.jie = weekNoteDetail;
                    weekday.classroomName = classroomName;
                    adata.weekdays.add(weekday);
                }
            }
            if (curriculum.course.stream().noneMatch(c -> Objects.equals(c.courseName, courseName))) {
                curriculum.course.add(co);
            }
        }
//        if (curriculum.nian == null) {
//            curriculum.nian = getSearch(jsonObj, "");
//        }
//        if (curriculum.startDate == null) {
//            curriculum.startDate = getSearch(jsonObj, "");
//        }
//        if (curriculum.zhouInt == null) {
//            curriculum.zhouInt = Integer.parseInt(getSearch(jsonObj, ""));
//        }
        return curriculum;
    }


    public static String getCurrentSemester() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        // 上半年（1~6月） → 去年-今年-2
        if (month >= 1 && month <= 6) {
            return String.format("%d-%d-2", year - 1, year);
        }
        // 下半年（7~12月） → 今年-明年-1
        else {
            return String.format("%d-%d-1", year, year + 1);
        }
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String getSemesterStartDate() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        LocalDate startDate;

        if (month >= 1 && month <= 6) {
            // 上半年 → 第二学期：次年2月20日
            startDate = LocalDate.of(year, 2, 20);
        } else {
            // 下半年 → 第一学期：当年9月1日
            startDate = LocalDate.of(year, 9, 1);
        }

        return startDate.format(FORMATTER);
    }
    private static String getSearch(JsonElement json, String expr) {
        if (expr == null || expr.isEmpty()) return null;
        try {
            Expression<JsonElement> exp = jmespath.compile(expr);
            JsonElement result = exp.search(json);
            return result != null && !result.isJsonNull() ? result.getAsString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取从开学日期到今天经过了多少周
     *
     * @param startDateStr 开学日期（格式 yyyy-MM-dd）
     * @return 已经过的周数（第1周算作1）
     */
    public static int getWeekSinceStart(String startDateStr) {
        // 解析开学日期
        LocalDate startDate = LocalDate.parse(startDateStr);
        // 获取今天日期
        LocalDate today = LocalDate.now();
        // 计算两个日期间相差的天数
        long daysBetween = ChronoUnit.DAYS.between(startDate, today);
        // 除以7得到周数（+1代表第一周从开学日开始）
        int weeks = (int) (daysBetween / 7) + 1;
        return Math.max(weeks, 1); // 防止负数
    }

    /**
     * 根据开学日期和当前周数，计算该周的周一到周日日期
     *
     * @param startDateStr 开学日期（yyyy-MM-dd）
     * @param currentWeek  当前周数（第1周 = 开学当周）
     * @return 包含周一到周日日期的字符串数组
     */
    public static String[] getWeekDates(String startDateStr, int currentWeek) {
        LocalDate startDate = LocalDate.parse(startDateStr);
        // 计算当前周的第一天（周一）
        LocalDate weekStart = startDate.plusWeeks(currentWeek - 1);
        // 日期格式
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String[] days = new String[7];
        for (int i = 0; i < 7; i++) {
            days[i] = weekStart.plusDays(i).format(fmt);
        }
        return days;
    }

    /**
     * 获取今天几月几日
     * @return yyyy-mm-dd
     */
    public static String getToday() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return now.format(formatter);
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
        public List<classWeekDetails> classWeekDetails;

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

    public static class classWeekDetails {
        public String weeks;               // 周次范围描述，如 "1"、"1-7"、"8,9,10,11"
        public List<Weekday> weekdays;     // 每周的上课日列表（可选，默认1-7）
    }

    public static class Weekday {
        public String jie;
        public String classroomName;
    }
}