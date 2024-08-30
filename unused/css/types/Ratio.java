package de.rccookie.css.types;

public interface Ratio {

    int getWidth();

    int getHeight();

    default double getRatio() {
        return getWidth() / (double) getHeight();
    }
}
