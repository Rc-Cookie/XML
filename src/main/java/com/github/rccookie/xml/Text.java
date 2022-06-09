package com.github.rccookie.xml;

import java.util.Collections;
import java.util.Objects;

public class Text extends Node {

    private String text;

    public Text(String text) {
        super("text", Collections.emptyMap(), Collections.emptyList());
        this.text = Objects.requireNonNull(text);
    }


    @Override
    void getText(StringBuilder str) {
        str.append(text);
    }

    public void setText(String text) {
        this.text = Objects.requireNonNull(text);
    }

    @Override
    void toString(StringBuilder str, boolean inner) {
        for(int i=0, stop=text.length(); i<stop; i++) {
            char c = text.charAt(i);
            if(c == '<') str.append("&lt;");
            else if(c == '>') str.append("&gt;");
            else if(c == '&') str.append("&amp;");
            else str.append(c);
        }
    }

    @Override
    boolean removeBlankText0() {
        if(text.isBlank()) {
            setParent(null);
            return true;
        }
        return false;
    }

    @Override
    void innerXML(StringBuilder str, boolean inner) {
        toString(str, inner);
    }

    @Override
    public void setInnerXML(String xml) {
        setText(xml
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&apos;", "'")
                .replace("&quot;", "\""));
    }
}
