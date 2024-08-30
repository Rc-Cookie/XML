package de.rccookie.css;

public enum AlignItems {
    STRETCH("stretch"),
    CENTER("center"),
    FLEX_START("flex-start"),
    FLEX_END("flex-end"),
    BASELINE("baseline"),
    INITIAL("initial"),
    INHERIT("inherit");

    private final String name;

    AlignItems(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static AlignItems fromCss(String cssName) {
        return valueOf(cssName.toUpperCase().replace('-', '_'));
    }
}
