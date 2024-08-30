package de.rccookie.css.types;

public interface Length extends LengthPercentage {

    @Override
    default Length resolve(Length total) {
        return this;
    }

    double getValue();

    Unit getUnit();

    enum Unit {
        CAP,
        CH,
        EM,
        EX,
        IC,
        LH,
        REM,
        RLH,

        VH,
        VW,
        VMAX,
        VMIN,
        VB,
        VI,
        SVH,
        SVW,
        SVMAX,
        SVMIN,
        SVB,
        SVI,
        LVH,
        LVW,
        LVMAX,
        LVMIN,
        LVB,
        LVI,
        DVH,
        DVW,
        DVMAX,
        DVMIN,
        DVB,
        DVI,

        CQW,
        CQH,
        CQI,
        CQB,
        CQMIN,
        CQMAX,

        PX,
        CM,
        MM,
        Q,
        IN,
        PC,
        PT;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

        public static Unit fromCss(String cssName) {
            return valueOf(cssName.toUpperCase());
        }
    }
}
