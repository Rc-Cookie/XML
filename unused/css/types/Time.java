package de.rccookie.css.types;

public interface Time extends TimePercentage {

    default double getSeconds() {
        return getMilliseconds() / 1000;
    }

    @Override
    default Time resolve(Time total) {
        return this;
    }

    double getMilliseconds();
}
