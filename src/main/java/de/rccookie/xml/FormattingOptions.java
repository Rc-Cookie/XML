package de.rccookie.xml;

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
     * Whether to write HTML void tags as self-closing tags.
     */
    final boolean selfCloseVoidTags;
    /**
     * Whether to write <code>&lt;br /></code> instead of <code>&lt;br/></code>.
     */
    final boolean spaceBeforeSelfClose;
    /**
     * Is '--' allowed within comments?
     */
    final boolean allowDoubleDashInComment;
    /**
     * Should errors (like uppercase xhtml tags) be fixed?
     */
    final boolean tryFixErrors;
    /**
     * Should html formatting be used? Relevant for void tags.
     */
    final boolean html;
    /**
     * Should xhtml formatting be used? Relevant for error checking.
     */
    final boolean xhtml;

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
        selfCloseVoidTags = (options & XML.SELF_CLOSE_VOID_TAGS) != 0;
        spaceBeforeSelfClose = (options & XML.SPACE_BEFORE_SELF_CLOSE) != 0;
        allowDoubleDashInComment = (options & XML.ALLOW_DOUBLE_DASH_IN_COMMENT) != 0;
        tryFixErrors = (options & XML.TRY_FIX_ERRORS) != 0;
        html = (options & XML.HTML_OPTION) != 0;
        xhtml = (options & XML.XHTML_OPTION) != 0;
        indent = formatted ? 0 : Integer.MIN_VALUE;
    }

    /**
     * Returns a copy with the given formatting preference, and indented if it is
     * formatted.
     *
     * @param options The formatting to copy from
     * @param formatted Whether the output should be formatted. If not, it will also not be indented
     */
    private FormattingOptions(FormattingOptions options, boolean formatted) {
        this.formatted = formatted;
        collapseEmpty = options.collapseEmpty;
        collapseInner = options.collapseInner;
        selfCloseVoidTags = options.selfCloseVoidTags;
        spaceBeforeSelfClose = options.spaceBeforeSelfClose;
        allowDoubleDashInComment = options.allowDoubleDashInComment;
        tryFixErrors = options.tryFixErrors;
        html = options.html;
        xhtml = options.xhtml;
        indent = formatted ? options.indent + 1 : Integer.MIN_VALUE;
    }

    /**
     * Returns a formatting options instance that is equal to this one, except
     * indented one level more.
     *
     * @return An indented formatting options instance
     */
    FormattingOptions indent() {
        if(!formatted) return this;
        return new FormattingOptions(this, true);
    }

    /**
     * Returns a formatting options instance that is equal to this one, except
     * <code>formatted</code> is set to false and any indent level is removed
     *
     * @return An unformatted formatting options instance
     */
    FormattingOptions noFormat() {
        if(!formatted) return this;
        return new FormattingOptions(this, false);
    }
}
