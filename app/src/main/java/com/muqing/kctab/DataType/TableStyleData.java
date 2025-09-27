package com.muqing.kctab.DataType;

import android.content.Context;

import com.muqing.gj;

import java.util.Map;
import java.util.Objects;

public class TableStyleData {

    public TableTime tableTime;
    public TableH tableH;
    public Table table;

    public static class TableTime extends ViewGridAttrs {
        public ViewAttrs tableTimetitle;
        public ViewAttrs starttime;
        public ViewAttrs endtime;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TableTime)) return false;
            TableTime tableTime = (TableTime) o;
            return Objects.equals(tableTimetitle, tableTime.tableTimetitle) && Objects.equals(starttime, tableTime.starttime) && Objects.equals(endtime, tableTime.endtime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tableTimetitle, starttime, endtime);
        }
    }

    public static class TableH extends ViewGridAttrs {
        public ViewAttrs tableHtitle;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TableH)) return false;
            TableH tableH = (TableH) o;
            return Objects.equals(tableHtitle, tableH.tableHtitle);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tableHtitle);
        }
    }

    public static class Table extends ViewGridAttrs {
        public ViewAttrs tabletitle;
        public ViewAttrs tablemessage;
        public ViewAttrs list_size;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Table)) return false;
            Table table = (Table) o;
            return Objects.equals(tabletitle, table.tabletitle) && Objects.equals(tablemessage, table.tablemessage) && Objects.equals(list_size, table.list_size);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tabletitle, tablemessage, list_size);
        }
    }

    public static class ViewAttrs {
        public String text;
        public String textColor;
        public String visibility;
        public int height;
        public Float textSize;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ViewAttrs)) return false;
            ViewAttrs viewAttrs = (ViewAttrs) o;
            return height == viewAttrs.height && Objects.equals(text, viewAttrs.text) && Objects.equals(textColor, viewAttrs.textColor) && Objects.equals(visibility, viewAttrs.visibility) && Objects.equals(textSize, viewAttrs.textSize);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text, textColor, visibility, height, textSize);
        }
    }

    /**
     * 基础控件属性
     */
    public static class ViewGridAttrs {
        public String background;
        private int height;

        // 用于存储 app: 开头的自定义属性，比如 cardBackgroundColor、cardElevation 等
        public Map<String, Object> appAttrs;
        public int getHeight(Context context) {
            return gj.dp2px(context, height);
        }

        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ViewGridAttrs)) return false;
            ViewGridAttrs that = (ViewGridAttrs) o;
            return height == that.height && Objects.equals(background, that.background) && Objects.equals(appAttrs, that.appAttrs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(background, height, appAttrs);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableStyleData)) return false;
        TableStyleData that = (TableStyleData) o;
        return Objects.equals(tableTime, that.tableTime) && Objects.equals(tableH, that.tableH) && Objects.equals(table, that.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableTime, tableH, table);
    }
}
