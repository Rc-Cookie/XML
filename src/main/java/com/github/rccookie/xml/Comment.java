package com.github.rccookie.xml;

import java.util.Collections;
import java.util.Objects;

public class Comment extends Node {

    private String comment;

    public Comment(String comment) {
        super(null, Collections.emptyMap(), Collections.emptyList());
        this.comment = Objects.requireNonNull(comment);
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if(comment.contains("--"))
            throw new XMLParseException("'--' is not allowed in XML comments");
        this.comment = Objects.requireNonNull(comment);
    }

    @Override
    void toString(StringBuilder str) {
        str.append("<!--").append(comment).append("-->");
    }

    @Override
    boolean removeBlankText0() {
        return false;
    }

    @Override
    void innerXML(StringBuilder str) {
        // Comment has no inner value
    }

    @Override
    public void setInnerXML(String xml) {
        throw new UnsupportedOperationException();
    }
}
