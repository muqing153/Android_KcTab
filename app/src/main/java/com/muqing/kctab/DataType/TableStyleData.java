package com.muqing.kctab.DataType;

import java.util.Objects;

public class TableStyleData {

    public String name;
    public boolean cardUseCompatPadding;
    public boolean style;

    public float cardElevation;
    public float cardCornerRadius;
    public float width;
    public float height;

    public TableStyleData() {
        name = "默认";
        style = true;
        cardUseCompatPadding = true;
        width = 80;
        height = 100;
        cardElevation = -1;
        cardCornerRadius = -1;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableStyleData)) return false;
        TableStyleData that = (TableStyleData) o;
        return cardUseCompatPadding == that.cardUseCompatPadding && style == that.style && Float.compare(cardElevation, that.cardElevation) == 0 && Float.compare(cardCornerRadius, that.cardCornerRadius) == 0 && Float.compare(width, that.width) == 0 && Float.compare(height, that.height) == 0 && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cardUseCompatPadding, style, cardElevation, cardCornerRadius, width, height);
    }
}
