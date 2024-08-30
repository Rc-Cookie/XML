package de.rccookie.css.types;

public interface Frequency extends FrequencyPercentage {

    @Override
    default Frequency resolve(Frequency total) {
        return this;
    }

    double getHZ();
}
