package de.rccookie.css.types;

public interface Percentage extends TimePercentage, LengthPercentage, FrequencyPercentage {

    double getPercentage();

    @Override
    default Time resolve(Time total) {
        return Time.ofMilliseconds(total.getMilliseconds() * getPercentage());
    }

    @Override
    default Length resolve(Length total) {
        return Length.of(total.getValue() * getPercentage(), total.getUnit());
    }

    @Override
    default Frequency resolve(Frequency total) {
        return Frequency.ofHZ(total.getHZ() * getPercentage());
    }
}
