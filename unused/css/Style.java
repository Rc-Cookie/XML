package de.rccookie.css;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

public abstract class Style {

    private final Map<String, String> data = new HashMap<>();

    public abstract Style getParent();

    public AlignContent getAlignContent() {
        String css = data.get("align-content");
        if(css != null) return AlignContent.fromCss(css);
        Style parent = getParent();
        return parent != null ? parent.getAlignContent() : null;
    }

    public void setAlignContent(@Nullable AlignContent alignContent) {
        if(alignContent != null)
            data.put("align-content", alignContent.toString());
        else data.remove("align-content");
    }

    public AlignItems getAlignItems() {
        String css = data.get("align-items");
        if(css != null) return AlignItems.fromCss(css);
        Style parent = getParent();
        return parent != null ? parent.getAlignItems() : null;
    }

    public void setAlignItems(@Nullable AlignItems alignContent) {
        if(alignContent != null)
            data.put("align-items", alignContent.toString());
        else data.remove("align-items");
    }

    public AlignSelf getAlignSelf() {
        String css = data.get("align-self");
        if(css != null) return AlignSelf.fromCss(css);
        Style parent = getParent();
        return parent != null ? parent.getAlignSelf() : null;
    }

    public void setAlignSelf(@Nullable AlignSelf alignContent) {
        if(alignContent != null)
            data.put("align-self", alignContent.toString());
        else data.remove("align-self");
    }

    public All getAll() {
        String css = data.get("all");
        if(css != null) return All.fromCss(css);
        Style parent = getParent();
        return parent != null ? parent.getAll() : null;
    }

    public void setAll(@Nullable All alignContent) {
        if(alignContent != null)
            data.put("all", alignContent.toString());
        else data.remove("all");
    }


}
