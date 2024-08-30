package de.rccookie.css.types;

public enum LineStyle {
    NONE,
    HIDDEN,
    DOTTED,
    DASHED,
    SOLID,
    DOUBLE,
    GROOVE,
    RIDGE,
    INSET,
    OUTSET;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static LineStyle fromCSS(String css) {
        return valueOf(css.toUpperCase());
    }
}
