package de.rccookie.css.types;

public enum NamedColor implements Color {
    ALICEBLUE(0xf0f8ff),
    ANTIQUEWHITE(0xfaebd7),
    AQUA(0x00ffff),
    AQUAMARINE(0x7fffd4),
    AZURE(0xf0ffff),
    BEIGE(0xf5f5dc),
    BISQUE(0xffe4c4),
    BLACK(0x000000),
    BLANCHEDALMOND(0xffebcd),
    BLUE(0x0000ff),
    BLUEVIOLET(0x8a2be2),
    BROWN(0xa52a2a),
    BURLYWOOD(0xdeb887),
    CADETBLUE(0x5f9ea0),
    CHARTREUSE(0x7fff00),
    CHOCOLATE(0xd2691e),
    CORAL(0xff7f50),
    CORNFLOWERBLUE(0x6495ed),
    CORNSILK(0xfff8dc),
    CRIMSON(0xdc143c),
    DARKBLUE(0x00008b),
    DARKCYAN(0x008b8b),
    DARKGOLDENROD(0xb8860b),
    DARKGRAY(0xa9a9a9),
    DARKGREEN(0x006400),
    DARKKHAKI(0xbdb76b),
    DARKMAGENTA(0x8b008b),
    DARKOLIVEGREEN(0x556b2f),
    DARKORANGE(0xff8c00),
    DARKORCHID(0x9932cc),
    DARKRED(0x8b0000),
    DARKSALMON(0xe9967a),
    DARKSEAGREEN(0x8fbc8f),
    DARKSLATEBLUE(0x483d8b),
    DARKSLATEGRAY(0x2f4f4f),
    DARKTURQUOISE(0x00ced1),
    DARKVIOLET(0x9400d3),
    DEEPPINK(0xff1493),
    DEEPSKYBLUE(0x00bfff),
    DIMGRAY(0x696969),
    DODGERBLUE(0x1e90ff),
    FIREBRICK(0xb22222),
    FLORALWHITE(0xfffaf0),
    FORESTGREEN(0x228b22),
    FUCHSIA(0xff00ff),
    GAINSBORO(0xdcdcdc),
    GHOSTWHITE(0xf8f8ff),
    GOLD(0xffd700),
    GOLDENROD(0xdaa520),
    GRAY(0x808080),
    GREEN(0x008000),
    GREENYELLOW(0xadff2f),
    HONEYDEW(0xf0fff0),
    HOTPINK(0xff69b4),
    INDIANRED(0xcd5c5c),
    INDIGO(0x4b0082),
    IVORY(0xfffff0),
    KHAKI(0xf0e68c),
    LAVENDER(0xe6e6fa),
    LAVENDERBLUSH(0xfff0f5),
    LAWNGREEN(0x7cfc00),
    LEMONCHIFFON(0xfffacd),
    LIGHTBLUE(0xadd8e6),
    LIGHTCORAL(0xf08080),
    LIGHTCYAN(0xe0ffff),
    LIGHTGOLDENRODYELLOW(0xfafad2),
    LIGHTGRAY(0xd3d3d3),
    LIGHTGREEN(0x90ee90),
    LIGHTPINK(0xffb6c1),
    LIGHTSALMON(0xffa07a),
    LIGHTSEAGREEN(0x20b2aa),
    LIGHTSKYBLUE(0x87cefa),
    LIGHTSLATEGRAY(0x778899),
    LIGHTSTEELBLUE(0xb0c4de),
    LIGHTYELLOW(0xffffe0),
    LIME(0x00ff00),
    LIMEGREEN(0x32cd32),
    LINEN(0xfaf0e6),
    MAROON(0x800000),
    MEDIUMAQUAMARINE(0x66cdaa),
    MEDIUMBLUE(0x0000cd),
    MEDIUMORCHID(0xba55d3),
    MEDIUMPURPLE(0x9370db),
    MEDIUMSEAGREEN(0x3cb371),
    MEDIUMSLATEBLUE(0x7b68ee),
    MEDIUMSPRINGGREEN(0x00fa9a),
    MEDIUMTURQUOISE(0x48d1cc),
    MEDIUMVIOLETRED(0xc71585),
    MIDNIGHTBLUE(0x191970),
    MINTCREAM(0xf5fffa),
    MISTYROSE(0xffe4e1),
    MOCCASIN(0xffe4b5),
    NAVAJOWHITE(0xffdead),
    NAVY(0x000080),
    OLDLACE(0xfdf5e6),
    OLIVE(0x808000),
    OLIVEDRAB(0x6b8e23),
    ORANGE(0xffa500),
    ORANGERED(0xff4500),
    ORCHID(0xda70d6),
    PALEGOLDENROD(0xeee8aa),
    PALEGREEN(0x98fb98),
    PALETURQUOISE(0xafeeee),
    PALEVIOLETRED(0xdb7093),
    PAPAYAWHIP(0xffefd5),
    PEACHPUFF(0xffdab9),
    PERU(0xcd853f),
    PINK(0xffc0cb),
    PLUM(0xdda0dd),
    POWDERBLUE(0xb0e0e6),
    PURPLE(0x800080),
    REBECCAPURPLE(0x663399),
    RED(0xff0000),
    ROSYBROWN(0xbc8f8f),
    ROYALBLUE(0x4169e1),
    SADDLEBROWN(0x8b4513),
    SALMON(0xfa8072),
    SANDYBROWN(0xf4a460),
    SEAGREEN(0x2e8b57),
    SEASHELL(0xfff5ee),
    SIENNA(0xa0522d),
    SILVER(0xc0c0c0),
    SKYBLUE(0x87ceeb),
    SLATEBLUE(0x6a5acd),
    SLATEGRAY(0x708090),
    SNOW(0xfffafa),
    SPRINGGREEN(0x00ff7f),
    STEELBLUE(0x4682b4),
    TAN(0xd2b48c),
    TEAL(0x008080),
    THISTLE(0xd8bfd8),
    TOMATO(0xff6347),
    TRANSPARENT(0, true),
    TURQUOISE(0x40e0d0),
    VIOLET(0xee82ee),
    WHEAT(0xf5deb3),
    WHITE(0xffffff),
    WHITESMOKE(0xf5f5f5),
    YELLOW(0xffff00),
    YELLOWGREEN(0x9acd32);

    public static final NamedColor CYAN = FUCHSIA;
    public static final NamedColor DARKGREY = DARKGRAY;
    public static final NamedColor DARKSLATEGREY = DARKSLATEGRAY;
    public static final NamedColor DIMGREY = DIMGRAY;
    public static final NamedColor LIGHTGREY = LIGHTGRAY;
    public static final NamedColor LIGHTSLATEGREY = LIGHTSLATEGRAY;
    public static final NamedColor GREY = GRAY;
    public static final NamedColor MAGENTA = FUCHSIA;
    public static final NamedColor SLATEGREY = SLATEGRAY;

    private final float r,g,b,a;

    NamedColor(int rgb) {
        this(rgb, false);
    }
    NamedColor(int rgba, boolean hasAlpha) {
        r = (rgba>>16 & 0xFF) / 255f;
        g = (rgba>>8  & 0xFF) / 255f;
        b = (rgba     & 0xFF) / 255f;
        a = hasAlpha ? (rgba>>24 & 0xFF) / 255f : 1;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    @Override
    public float getRed() {
        return r;
    }

    @Override
    public float getGreen() {
        return g;
    }

    @Override
    public float getBlue() {
        return b;
    }

    @Override
    public float getAlpha() {
        return a;
    }

    public static NamedColor fromCSS(String css) {
        switch (css) {
            case "cyan": return CYAN;
            case "darkgrey": return DARKGREY;
            case "darkslategrey": return DARKSLATEGREY;
            case "dimgrey": return DIMGREY;
            case "lightgrey": return LIGHTGREY;
            case "lightslategrey": return LIGHTSLATEGREY;
            case "grey": return GREY;
            case "magenta": return MAGENTA;
            case "slategrey": return SLATEGREY;
            default: return valueOf(css.toUpperCase());
        }
    }
}
