package com.github.rccookie.xml;

import java.util.Collections;

import com.github.rccookie.util.Arguments;

public class Text extends Node {

    private String text;

    public Text(String text) {
        super("text", AttributeMap.EMPTY, Collections.emptyList());
        this.text = Arguments.checkNull(text);
    }


    @Override
    void getText(StringBuilder str) {
        str.append(text);
    }

    public void setText(String text) {
        this.text = Arguments.checkNull(text);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    void toString(StringBuilder str, int indent, boolean html, boolean inner) {
        boolean parentCode = parent != null && parent.tag.equals("code");
        String text = html && !parentCode ? this.text.replaceAll("\\s+", " ") : this.text;
        if(parent != null && parent.tag.equals("script")) str.append(text.replace("</script>", "</script\\>"));
        else if(!parentCode && indent >= 0) str.append(XMLEncoder.encode(text).replace("\n", '\n' + "  ".repeat(indent)));
        else XMLEncoder.encode(text, str);
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
    void innerXML(StringBuilder str, int indent, boolean html, boolean inner) {
        toString(str, indent, html, inner);
    }

    @Override
    public void setInnerXML(String xml) {
        setText(XMLEncoder.decode(xml));
    }
}
