package de.rccookie.css.types;

public enum GenericFamily {
    SERIF,
    SANS_SERIF,
    MONOSPACE,
    CURSIVE,
    FANTASY,
    SYSTEM_UI,
    UI_SERIF,
    UI_SANS_SERIF,
    UI_MONOSPACE,
    UI_ROUNDED,
    EMOJI,
    MATH,
    FANGSONG;

    @Override
    public java.lang.String toString() {
        return name().toLowerCase().replace('_', '-');
    }

    public static GenericFamily fromCSS(java.lang.String css) {
        return valueOf(css.toUpperCase().replace('-', '_'));
    }
}
