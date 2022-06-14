package com.github.rccookie.xml;

/**
 * Formatting options for printing xml.
 */
final class FormattingOptions {

    /**
     * Should the xml string be formatted with newlines and indents?
     */
    final boolean formatted;
    /**
     * Should empty nodes be collapsed using />?
     */
    final boolean collapseEmpty;
    /**
     * Should the content of the node be printed?
     */
    final boolean collapseInner;
    /**
     * Should html formatting be used? Relevant for void tags.
     */
    final boolean html;

    /**
     * The current indent. Undefined if {@link #formatted} is false.
     */
    final int indent;

    /**
     * Creates a new formatting options instance from the given options.
     *
     * @param options The options, see {@link XML}
     */
    FormattingOptions(long options) {
        formatted = (options & XML.FORMATTED) != 0;
        collapseEmpty = (options & XML.COLLAPSE_EMPTY) != 0;
        collapseInner = (options & XML.COLLAPSE_INNER) != 0;
        html = (options & XML.HTML_OPTION) != 0;
        indent = formatted ? 0 : Integer.MIN_VALUE;
    }

    /**
     * Creates an indented copy of the given formatting options.
     *
     * @param toBeIndented The formatting to copy indented
     */
    private FormattingOptions(FormattingOptions toBeIndented) {
        formatted = toBeIndented.formatted;
        collapseEmpty = toBeIndented.collapseEmpty;
        collapseInner = toBeIndented.collapseInner;
        html = toBeIndented.html;
        indent = formatted ? toBeIndented.indent + 1 : Integer.MIN_VALUE;
    }

    /**
     * Returns a formatting options instance that is equal to this one, except
     * indented one level more.
     *
     * @return An indented formatting options instance
     */
    FormattingOptions indent() {
        if(!formatted) return this;
        return new FormattingOptions(this);
    }
}
