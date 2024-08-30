package de.rccookie.css;

public enum All {
    INITIAL("initial"),
    INHERIT("inherit"),
    UNSET("unset");

    private final String name;

    All(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static All fromCss(String cssName) {
        return valueOf(cssName.toUpperCase());
    }
}
