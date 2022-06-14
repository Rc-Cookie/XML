package com.github.rccookie.xml;

import java.util.Collections;
import java.util.Objects;

import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * A comment node.
 */
public class Comment extends Node {

    /**
     * The comment text.
     */
    @NotNull
    private String comment;

    /**
     * Creates a new comment node.
     *
     * @param comment The comment string
     */
    public Comment(String comment) {
        super("", AttributeMap.EMPTY, Collections.emptyList());
        this.comment = Arguments.checkNull(comment);
    }


    @Override
    public @NotNull Comment clone() {
        return new Comment(comment);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Comment && ((Comment) o).comment.equals(comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comment);
    }

    /**
     * Returns the comment's text.
     *
     * @return The comment
     */
    @NotNull
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment's text.
     *
     * @param comment The text to set
     */
    public void setComment(@NotNull String comment) {
        if(Arguments.checkNull(comment, "comment").contains("--"))
            throw new XMLParseException("'--' is not allowed in XML comments");
        this.comment = Arguments.checkNull(comment);
    }

    @Override
    void toString(StringBuilder str, FormattingOptions options) {
        str.append("<!--").append(comment).append("-->");
    }

    @Override
    boolean removeBlankText0() {
        return false;
    }

    @Override
    void innerXML(StringBuilder str, FormattingOptions options) {
        // Comment has no inner value
    }

    @Override
    public void setInnerXML(@NotNull String xml, long options) {
        throw new UnsupportedOperationException("Comment");
    }
}
