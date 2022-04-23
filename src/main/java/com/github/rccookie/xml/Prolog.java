package com.github.rccookie.xml;

import java.util.Collections;
import java.util.Objects;

public class Prolog extends Node {


    public Prolog(String tag) {
        super(Objects.requireNonNull(tag), null, Collections.emptyList());
    }


    @Override
    void toString(StringBuilder str) {
        str.append("<?").append(tag);
        attributes.forEach((k,v) -> str.append(' ').append(k).append('=').append('"').append(v.replace("\"", "&quot;")).append('"'));
        str.append("?>");
    }

    @Override
    void innerXML(StringBuilder str) {
        // No inner value
    }

    @Override
    public void setInnerXML(String xml) {
        throw new UnsupportedOperationException();
    }
}
