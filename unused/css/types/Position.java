package de.rccookie.css.types;

public interface Position {

    LengthPercentage getX();

    LengthPercentage getY();

    Anchor getAnchorX();

    Anchor getAnchorY();


    enum Anchor implements LengthPercentage {
        MIN(0),
        CENTER(0.5),
        MAX(1);

        public static final Anchor LEFT = MIN;
        public static final Anchor RIGHT = MAX;
        public static final Anchor TOP = MIN;
        public static final Anchor BOTTOM = MAX;

        private final double percentage;

        Anchor(double percentage) {
            this.percentage = percentage;
        }

        @Override
        public Length resolve(Length total) {
            return Length.of(total.getValue() * percentage, total.getUnit());
        }
    }
}
