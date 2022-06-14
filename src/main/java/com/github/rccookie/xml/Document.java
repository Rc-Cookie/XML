package com.github.rccookie.xml;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The document node serves as the root node of an xml tree. It may also contain
 * an xml declaration and a doctype.
 */
public class Document extends Node {

    /**
     * The xml declaration.
     */
    private XMLDeclaration xmlDeclaration;
    /**
     * The doctype node.
     */
    private Doctype doctype;

    /**
     * Creates a new, empty document node.
     */
    public Document() {
        super("", AttributeMap.EMPTY, null);
    }

    @Override
    public @NotNull Document clone() {
        Document copy = new Document();
        for(Node child : children)
            copy.children.add(child.clone());
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Document)) return false;
        Document d = (Document) o;
        return children.equals(d.children) && Objects.equals(xmlDeclaration, d.xmlDeclaration) && Objects.equals(doctype, d.doctype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children, xmlDeclaration, doctype);
    }

    /**
     * Returns the document's xml declaration, if present.
     *
     * @return The xml declaration, or null
     */
    public XMLDeclaration getXMLDeclaration() {
        return xmlDeclaration;
    }

    /**
     * Returns the doctype declaration node, if present.
     *
     * @return The doctype node, or null
     */
    public Doctype getDoctype() {
        return doctype;
    }

    /**
     * Sets the xml declaration to use.
     *
     * @param xmlDeclaration The xml declaration to use
     */
    public void setXMLDeclaration(@Nullable XMLDeclaration xmlDeclaration) {
        this.xmlDeclaration = xmlDeclaration;
    }

    /**
     * Sets the doctype declaration node to use.
     *
     * @param doctype The doctype node to use
     */
    public void setDoctype(@Nullable Doctype doctype) {
        this.doctype = doctype;
    }

    @Override
    void toString(StringBuilder str, FormattingOptions options) {
        if(xmlDeclaration != null) {
            xmlDeclaration.toString(str, options);
            if(options.formatted) str.append('\n');
        }
        if(doctype != null) {
            doctype.toString(str, options);
            if(options.formatted) str.append('\n');
        }
        innerXML(str, options);
    }

    /**
     * The document node is always the root node.
     *
     * @return {@code null}
     */
    @Override
    public Node getParent() {
        return null;
    }

    /**
     * The document is always the root node, the parent cannot be changed.
     *
     * @param parent The node to set as parent
     * @throws UnsupportedOperationException Always
     */
    @Override
    @Contract("_->fail")
    public void setParent(@Nullable Node parent) {
        throw new UnsupportedOperationException("Document is always root");
    }
}
