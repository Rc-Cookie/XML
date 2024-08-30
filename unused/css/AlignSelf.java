package de.rccookie.css;

public enum AlignSelf {
    AUTO("auto"),
    STRETCH("stretch"),
    CENTER("center"),
    FLEX_START("flex-start"),
    FLEX_END("flex-end"),
    BASELINE("baseline"),
    INITIAL("initial"),
    INHERIT("inherit");

    private final String name;

    AlignSelf(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static AlignSelf fromCss(String cssName) {
        return valueOf(cssName.toUpperCase().replace('-', '_'));
    }
}
