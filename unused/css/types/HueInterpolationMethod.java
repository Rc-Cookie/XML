package de.rccookie.css.types;

public enum HueInterpolationMethod {
    SHORTER,
    LONGER,
    INCREASING,
    DECREASING;

    @Override
    public java.lang.String toString() {
        return name().toLowerCase() + " hue";
    }

    public static HueInterpolationMethod fromCSS(java.lang.String css) {
        if(!css.endsWith(" hue"))
            throw new IllegalArgumentException("'"+css+"' does not end with ' hue'");
        return valueOf(css.substring(0, css.length() - 4).toUpperCase());
    }
}
