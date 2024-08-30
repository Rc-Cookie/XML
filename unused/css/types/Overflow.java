package de.rccookie.css.types;

public enum Overflow {
    VISIBLE,
    HIDDEN,
    CLIP,
    SCROLL,
    AUTO;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static Overflow fromCSS(String css) {
        if(css.equals("overlay")) return AUTO;
        return valueOf(css.toUpperCase());
    }
}
