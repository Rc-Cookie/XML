package com.github.rccookie.xml;

import java.util.Collections;

import com.github.rccookie.util.Arguments;

public class Prolog extends Node {


    public Prolog(String tag) {
        super(Arguments.checkNull(tag), null, Collections.emptyList());
    }


    @Override
    void toString(StringBuilder str, int indent, boolean html, boolean inner) {
        str.append("<?").append(tag);
        attributes.forEach((k,v) -> str.append(' ').append(k).append('=').append('"').append(v.replace("\"", "&quot;")).append('"'));
        str.append("?>");
    }

    @Override
    void innerXML(StringBuilder str, int indent, boolean html, boolean inner) {
        // No inner value
    }

    @Override
    public void setInnerXML(String xml) {
        throw new UnsupportedOperationException();
    }
}
