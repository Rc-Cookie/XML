package de.rccookie.xml;

import java.util.Arrays;
import java.util.Objects;

import de.rccookie.json.Json;
import de.rccookie.json.JsonObject;
import de.rccookie.util.Arguments;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The document node serves as the root node of an xml tree. It may also contain
 * an xml declaration and a doctype.
 */
public class Document extends Node {

    static {
        Json.registerDeserializer(Document.class, json -> new Document(
                json.get("xmlDeclaration").as(XMLDeclaration.class),
                json.get("doctype").as(Doctype.class),
                json.get("children").as(Node[].class)
        ));
    }

    /**
     * The xml declaration.
     */
    private XMLDeclaration xmlDeclaration;
    /**
     * The doctype node.
     */
    private Doctype doctype;

    private W3cDocumentView view = null;

    /**
     * Creates a new, empty document node.
     */
    public Document() {
        super("", AttributeMap.EMPTY, null);
    }

    /**
     * Convenience constructor to wrap the given set of node in a document.
     * This is equivalent to <code>new Document().children.add(content)</code>
     *
     * @param content The content of the document
     */
    public Document(Node content) {
        this();
        children.add(content);
    }

    /**
     * Convenience constructor to wrap the given set of nodes in a document.
     * This is equivalent to <code>new Document().children.addAll(Arrays.asList(content))</code>
     *
     * @param content The contents of the document
     */
    public Document(Node... content) {
        this();
        children.addAll(Arrays.asList(content));
    }

    /**
     * Convenience constructor to create a new document with the specified xml declaration,
     * doctype and child nodes.
     *
     * @param xmlDeclaration The xml declaration to use, if any
     * @param doctype The doctype to use, if any
     * @param content The contents of the document
     */
    public Document(@Nullable XMLDeclaration xmlDeclaration, @Nullable Doctype doctype, Node... content) {
        this(content);
        setXMLDeclaration(xmlDeclaration);
        setDoctype(doctype);
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
     * Returns the root element in this document, or <code>null</code> if none is present.
     * If multiple root nodes are present (which is generally not allowed in xml documents),
     * the one with the tag name found in the doctype will be returned. If there are multiple
     * of these nodes, or no doctype is present, any of those nodes may be returned.
     *
     * @return The child node of this document
     */
    public Node rootNode() {
        if(children.isEmpty()) return null;
        if(children.size() == 1 || doctype == null) return child(0);
        return children.stream().filter(n -> n.tag.equals(doctype.getRootElement())).findAny().orElse(child(0));
    }

    /**
     * Sets the xml declaration to use.
     *
     * @param xmlDeclaration The xml declaration to use
     */
    public void setXMLDeclaration(@Nullable XMLDeclaration xmlDeclaration) {
        if(xmlDeclaration != null && xmlDeclaration.parent != null)
            xmlDeclaration.getParent().setXMLDeclaration(null);
        if(this.xmlDeclaration != null)
            this.xmlDeclaration.parent = null;
        this.xmlDeclaration = xmlDeclaration;
        if(xmlDeclaration != null)
            xmlDeclaration.parent = this;
    }

    /**
     * Sets the doctype declaration node to use.
     *
     * @param doctype The doctype node to use
     */
    public void setDoctype(@Nullable Doctype doctype) {
        if(doctype != null && doctype.parent != null)
            doctype.getParent().setDoctype(null);
        if(this.doctype != null)
            this.doctype.parent = null;
        this.doctype = doctype;
        if(doctype != null)
            doctype.parent = this;
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

    @Override
    void innerXML(StringBuilder str, FormattingOptions options) {
        if(options.xhtml) {
            Node[] withoutBlank = children.stream().filter(n -> !(n instanceof Text) || !n.text().isBlank()).toArray(Node[]::new);

            if(withoutBlank.length != 1 || (!withoutBlank[0].tag.equals("html") && (!options.tryFixErrors || !withoutBlank[0].tag.equalsIgnoreCase("html"))))
                throw new IllegalStateException("XHTML document without <html> node or other root top level tags");
            Node html = withoutBlank[0];

            boolean addRemoveXmlNS = !html.attributes.containsKey("xmlns");
            if(addRemoveXmlNS)
                html.attributes.put("xmlns", "http://www.w3.org/1999/xhtml");
            super.innerXML(str, options);
            if(addRemoveXmlNS)
                html.attributes.remove("xmlns");
        }
        else if(options.html) {
            Node[] withoutBlank = children.stream().filter(n -> !(n instanceof Text) || !n.text().isBlank()).toArray(Node[]::new);

            if(withoutBlank.length == 0 || !withoutBlank[0].tag.equalsIgnoreCase("html"))
                super.innerXML(str, options);
            else {
//                throw new IllegalStateException("HTML document without <html> node");
                Node html = withoutBlank[0];
                if(html.attributes.remove("xmlns", "http://www.w3.org/1999/xhtml")) {
                    super.innerXML(str, options);
                    html.attributes.put("xmlns", "http://www.w3.org/1999/xhtml");
                }
                else super.innerXML(str, options);
            }
        }
        else super.innerXML(str, options);
    }

    @Override
    public Object toJson() {
        JsonObject json = new JsonObject();
        if(!children.isEmpty())
            json.put("children", children);
        if(xmlDeclaration != null)
            json.put("xmlDeclaration", xmlDeclaration);
        if(doctype != null)
            json.put("doctype", doctype);
        return json;
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
     * The document node is always the root node.
     *
     * @return {@code this}
     */
    @Override
    public Document getRoot() {
        return this;
    }

    /**
     * The document node itself.
     *
     * @return {@code this}
     */
    @Override
    public Document getDocument() {
        return this;
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


    /**
     * Returns a new document parsed from the given xml. Unlike for normal nodes, the
     * xml string may contain multiple root nodes. This is equivalent to
     * {@link XML#parse(String)} and does not modify this document.
     *
     * @param xml The xml string to use as outer xml
     * @return The new document
     */
    @Override
    public Document withOuterXML(@NotNull String xml) {
        return withOuterXML(xml, XML.XML);
    }

    /**
     * Returns a new document parsed from the given html. Unlike for normal nodes, the
     * html string may contain multiple root nodes. This is equivalent to
     * <code>XML.parse(html, XML.HTML)</code> and does not modify this document.
     *
     * @param html The html string to use as outer html
     * @return The new document
     */
    @Override
    public Node withOuterHTML(@NotNull String html) {
        return withOuterXML(html, XML.HTML);
    }

    /**
     * Returns a new document parsed from the given xml. Unlike for normal nodes, the
     * xml string may contain multiple root nodes. This is equivalent to
     * {@link XML#parse(String,long)} and does not modify this document.
     *
     * @param xml The xml string to use as outer xml
     * @param options Parsing options, see {@link XML}
     * @return The new document
     */
    @Override
    public Document withOuterXML(@NotNull String xml, long options) {
        return XML.parse(xml, options);
    }

    /**
     * Sets this document to the document defined by the given xml string. Unlike for normal nodes, the
     * xml string may contain multiple root nodes. If an exception occurs during parsing,
     * this document will not be changed.
     *
     * @param xml The xml string to use as outer xml
     */
    @Override
    public void setOuterXML(@NotNull String xml) {
        setOuterXML(xml, XML.XML);
    }

    /**
     * Sets this document to the document defined by the given html string. Unlike for normal nodes, the
     * html string may contain multiple root nodes. If an exception occurs during parsing,
     * this document will not be changed.
     *
     * @param html The html string to use as outer html
     */
    @Override
    public void setOuterHTML(@NotNull String html) {
        setOuterXML(html, XML.HTML);
    }

    /**
     * Sets this document to the document defined by the given xml string. Unlike for normal nodes, the
     * xml string may contain multiple root nodes. If an exception occurs during parsing,
     * this document will not be changed.
     *
     * @param xml The xml string to use as outer xml
     * @param options Parsing options, see {@link XML}
     */
    @Override
    public void setOuterXML(@NotNull String xml, long options) {
        Arguments.checkNull(xml, "xml");
        Document newDocument = XML.parse(xml, options);
        children.clear();
        children.addAll(newDocument.children); // Should not cause an exception - all nodes have been freshly parsed, should be unrelated instances
    }


    @Override
    protected org.w3c.dom.Document asW3cNode() {
        if(view == null)
            view = new W3cDocumentView(this);
        return view;
    }

    /**
     * Returns an immutable view of this document as a {@link org.w3c.dom.Document}.
     *
     * @return This document as a <code>org.w3c.dom.Document</code>
     */
    public org.w3c.dom.Document asW3cDocument() {
        return asW3cNode();
    }


    /**
     * Returns a new document with html structure. This includes the nodes
     * <code>head</code> and <code>body</code>. The returned document does not
     * include a title element.
     *
     * @param bodyElements Child nodes for the body element
     * @return A new html document
     */
    public static Document newDefaultHtml(Node... bodyElements) {
        return newDefaultHtml(null, bodyElements);
    }

    /**
     * Returns a new document with html structure. This includes the nodes
     * <code>head</code> and <code>body</code>. The returned document will include
     * a title element iff a non-null value for the title is given.
     *
     * @param title The value for the <code>head/title</code> field, or <code>null</code>
     *              which will generate no title element
     * @param bodyElements Child nodes for the body element
     * @return A new html document
     */
    public static Document newDefaultHtml(@Nullable String title, Node... bodyElements) {
        return new Document(
                null,
                Doctype.defaultHtml(),
                new Node("html",
                        title != null ? new Node("head", new Node("title", new Text(title))) : new Node("head"),
                        new Node("body", bodyElements)
                )
        );
    }

    /**
     * Returns a new document with xhtml structure.
     *
     * @param title The value for the <code>head/title</code> field, mandatory in xhtml
     * @param bodyElements Child nodes for the body element
     * @return A new xhtml document
     */
    public static Document newDefaultXhtml(String title, Node... bodyElements) {
        Document document = new Document(
                new XMLDeclaration("1.0", "UTF-8", false),
                Doctype.defaultXhtml(),
                new Node("html",
                        new Node("head",
                                new Node("title", new Text(title))
                        ),
                        new Node("body", bodyElements)
                )
        );
        document.rootNode().attributes.put("xmlns", "http://www.w3.org/1999/xhtml");
        return document;
    }

    /**
     * Returns a new document with html or xhtml structure. This includes the nodes
     * <code>head</code> and <code>body</code>, and a title. Note that the title is
     * mandatory for xhtml documents, but optional for html documents.
     *
     * @param xhtml Whether to use the xhtml standard
     * @param title The value for the <code>head/title</code> field
     * @param bodyElements Child nodes for the body element
     * @return A new html or xhtml document
     */
    public static Document newDefaultHtmlOrXhtml(boolean xhtml, String title, Node... bodyElements) {
        return xhtml ? newDefaultXhtml(title, bodyElements) : newDefaultHtml(title, bodyElements);
    }

    /**
     * Returns a new document with svg structure. The svg version will be set to <code>1.1</code>.
     *
     * @param children Child nodes to add to the root svg node
     * @return A new svg document
     */
    public static Document newDefaultSVG(Node... children) {
        Document document = new Document(
                new XMLDeclaration("1.0", "UTF-8", false),
                Doctype.defaultSvg(),
                new Node("svg")
        );
        document.rootNode().attributes.put("xmlns", "http://www.w3.org/2000/svg");
        document.rootNode().attributes.put("xmlns:xlink", "http://www.w3.org/1999/xlink");
        document.rootNode().attributes.put("version", "1.1");

        document.rootNode().children.addAll(children);
        return document;
    }
}
