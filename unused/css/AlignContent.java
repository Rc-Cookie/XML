package de.rccookie.css;

public enum AlignContent {
    STRETCH("stretch"),
    CENTER("center"),
    FLEX_START("flex-start"),
    FLEX_END("flex-end"),
    SPACE_BETWEEN("space-between"),
    SPACE_AROUND("space-around"),
    INITIAL("initial"),
    INHERIT("inherit");

    private final String name;

    AlignContent(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static AlignContent fromCss(String cssName) {
        return valueOf(cssName.toUpperCase().replace('-', '_'));
    }
}
