package de.rccookie.css.types;

import java.util.HashMap;
import java.util.Map;

public enum SystemColor implements Color {
    ACCENT_COLOR,
    ACCENT_COLOR_TEXT,
    ACTIVE_TEXT,
    BUTTON_BORDER,
    BUTTON_FACE,
    BUTTON_TEXT,
    CANVAS,
    CANVAS_TEXT,
    FIELD,
    FIELD_TEXT,
    GRAY_TEXT,
    HIGHLIGHT,
    HIGHLIGHT_TEXT,
    LINK_TEXT,
    MARK,
    MARK_TEXT,
    VISITED_TEXT,

    ACTIVE_BORDER,
    ACTIVE_CAPTION,
    APP_WORKSPACE,
    BACKGROUND,
    BUTTON_HIGHLIGHT,
    BUTTON_SHADOW,
    CAPTION_TEXT,
    INACTIVE_BORDER,
    INACTIVE_CAPTION,
    INACTIVE_CAPTION_TEXT,
    INFO_BACKGROUND,
    INFO_TEXT,
    MENU,
    MENU_TEXT,
    SCROLLBAR,
    THREE_D_DARK_SHADOW,
    THREE_D_FACE,
    THREE_D_HIGHLIGHT,
    THREE_D_LIGHT_SHADOW,
    THREE_D_SHADOW,
    WINDOW,
    WINDOW_FRAME,
    WINDOW_TEXT;

    private static final Map<String, SystemColor> byCSSName = new HashMap<>();
    static {
        for(SystemColor c : values())
            byCSSName.put(c.toString(), c);
    }

    @Override
    public String toString() {
        return name().replace("_", "");
    }

    @Override
    public float getRed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getGreen() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getBlue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getAlpha() {
        throw new UnsupportedOperationException();
    }

    public static SystemColor fromCSS(String css) {
        return byCSSName.get(css.toUpperCase());
    }
}
