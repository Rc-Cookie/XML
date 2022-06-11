package com.github.rccookie.xml;

import java.util.Collections;

import com.github.rccookie.util.Arguments;

public class Comment extends Node {

    private String comment;

    public Comment(String comment) {
        super(null, AttributeMap.EMPTY, Collections.emptyList());
        this.comment = Arguments.checkNull(comment);
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if(comment.contains("--"))
            throw new XMLParseException("'--' is not allowed in XML comments");
        this.comment = Arguments.checkNull(comment);
    }

    @Override
    void toString(StringBuilder str, int indent, boolean html, boolean inner) {
        str.append("<!--").append(comment).append("-->");
    }

    @Override
    boolean removeBlankText0() {
        return false;
    }

    @Override
    void innerXML(StringBuilder str, int indent, boolean html, boolean inner) {
        // Comment has no inner value
    }

    @Override
    public void setInnerXML(String xml) {
        throw new UnsupportedOperationException();
    }
}
