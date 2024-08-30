package de.rccookie.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.rccookie.json.Json;
import de.rccookie.json.JsonObject;
import de.rccookie.json.JsonSerializable;
import de.rccookie.util.Arguments;
import de.rccookie.util.Cloneable;
import de.rccookie.util.IterableIterator;
import de.rccookie.util.ListStream;
import de.rccookie.util.Utils;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

/**
 * Represents a node in an xml tree. A node has a tag, attributes and children.
 * It also holds a reference to its parent. Nodes also describe the text sections
 * in the document, with the special subclass {@link Text}.
 */
public class Node implements Iterable<Node>, Cloneable<Node>, JsonSerializable {

    private static final Pattern CLASS_SPLIT_PAT = Pattern.compile("\\s+");

    static {
        Json.registerDeserializer(Node.class, json -> {
            if(json.isString())
                return json.as(Text.class);
            if(json.isArray())
                return json.as(Document.class);
            String tag = json.getString("tag");
            if(tag.equals("!DOCTYPE"))
                return json.as(Doctype.class);
            if(tag.startsWith("?"))
                return json.as(Prolog.class);

            Node node = new Node(tag);
            node.attributes.putAll(json.get("attributes").asMap(String.class));
            node.children.addAll(json.get("children").asList(Node.class));
            return node;
        });
    }

    /**
     * The tag name of the node.
     */
    @NotNull
    public final String tag;
    /**
     * Attributes of the node. Null keys and values are <i>not</i> permitted.
     */
    @NotNull
    public final AttributeMap attributes;
    /**
     * Child nodes of this node. This also includes text nodes that are within
     * this node. Edit this list to add or remove children.
     */
    @NotNull
    public final NodeList children;

    /**
     * Parent node of this node.
     */
    @Nullable
    Node parent = null;

    W3cElementView view = null;


    /**
     * Creates a new node with the given tag name.
     *
     * @param tag The tag name for the node
     */
    public Node(@NotNull String tag) {
        this(tag, null, null);
    }

    /**
     * Creates a new node with the given tag name and adds all the specified child nodes to it.
     *
     * @param tag The tag name for the node
     * @param children The children to add to the node
     */
    public Node(@NotNull String tag, Node... children) {
        this(tag);
        this.children.addAll(Arrays.asList(Arguments.deepCheckNull(children, "children")));
    }

    /**
     * Creates a new node.
     *
     * @param tag The tag name for the node
     * @param attributes The attribute map instance to use, null will use the
     *                   default implementation
     * @param children The children list for the node, null will use the default
     *                 implementation
     */
    Node(@NotNull String tag, AttributeMap attributes, List<Node> children) {
        this.tag = Arguments.checkNull(tag, "tag");
        this.attributes = attributes != null ? attributes : new AttributeMap();
        this.children = children != null ? new NodeList(children, this) : new NodeList(this);
    }


    /**
     * Creates a deep clone of this node (child nodes will also be cloned).
     *
     * @return A clone of this node
     */
    @Override
    public @NotNull Node clone() {
        Node copy = new Node(tag, attributes.clone(), null);
        for(Node child : children)
            copy.children.add(child.clone());
        return copy;
    }

    /**
     * Returns a short string representation of this node. <b>This is not valid
     * xml and does not include child nodes!</b> The output is the opening and closing
     * tag of this node with attributes, but if there are children, they will just be
     * shown as "...".
     *
     * @return A string representation of this node
     */
    @Override
    public String toString() {
        return toXML(XML.XML|XML.COLLAPSE_INNER|XML.FORMATTED);
    }

    /**
     * Checks if the given node is equal to this node. The parents don't have to be
     * equal, children <b>do</b> have to be equal as well.
     *
     * @param o The object to compare
     * @return Whether this node is equal to the given object
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o.getClass() != getClass()) return false;
        Node node = (Node) o;
        return tag.equals(node.tag) && attributes.equals(node.attributes) && children.equals(node.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, attributes, children);
    }

    /**
     * Returns this node as formatted (indented) xml string.
     *
     * @return This node as xml string
     */
    @NotNull
    @Language("xml")
    public String toXML() {
        return toXML(XML.FORMATTED);
    }

    /**
     * Returns this node as xml string using the given formatting options. By
     * default, this will not add intents and newlines.
     *
     * @param options Formatting options, see {@link XML}
     * @return This node as xml string
     */
    @NotNull
    public String toXML(long options) {
        return toString(options|XML.XML);
    }

    /**
     * Returns this node as formatted (indented) html string.
     *
     * @return This node as html string
     */
    @NotNull
    @Language("html")
    public String toHTML() {
        return toHTML(XML.FORMATTED);
    }

    /**
     * Returns this node as html string using the given formatting options. By
     * default, this will not add intents and newlines.
     *
     * @param options Formatting options, see {@link XML}
     * @return This node as html string
     */
    @NotNull
    @Language("html")
    public String toHTML(long options) {
        return toString(options|XML.HTML);
    }

    /**
     * Returns this node as xml string using the given formatting options. By
     * default, this will not add intents and newlines.
     *
     * @param options Formatting options, see {@link XML}
     * @return This node as xml string
     */
    @NotNull
    public String toString(long options) {
        StringBuilder str = new StringBuilder();
        toString(str, new FormattingOptions(options));
        return str.toString();
    }

    /**
     * Appends this node as xml string to the given StringBuilder, using the
     * specified formatting.
     *
     * @param str The string builder to write into
     * @param options The formatting to use
     */
    void toString(StringBuilder str, FormattingOptions options) {

        str.append('<');
        if(options.xhtml) {
            String lowerTag = tag.toLowerCase();
            if(!options.tryFixErrors && !lowerTag.equals(tag))
                throw new IllegalStateException("Uppercase tags are not allowed in XHTML, found '"+tag+"'");
            str.append(tag);
        }
        else str.append(tag);
        attributes.toString(str, options);

        if(children.isEmpty()) {
            if(options.html && XMLParser.HTML_VOID_TAGS.contains(tag.toLowerCase())) {
                if(options.selfCloseVoidTags) {
                    if(options.spaceBeforeSelfClose)
                        str.append(' ');
                    str.append('/');
                }
                str.append('>');
                return;
            }
            else if(options.collapseEmpty) {
                if(options.spaceBeforeSelfClose)
                    str.append(' ');
                str.append("/>");
                return;
            }
        }

        str.append('>');

        options = (options.html || options.xhtml) && (tag.equalsIgnoreCase("pre")) ? options.noFormat() : options;

        boolean newline = options.formatted && !options.collapseInner && ((children.size() == 1 && (!(children.get(0) instanceof Text) || (children.get(0).text().contains("\n")))) || children.size() > 1);
        if(newline) str.append('\n').append("  ".repeat(options.indent+1));

        if(options.collapseInner && !children.isEmpty())
            str.append("...");
        else innerXML(str, options.indent());

        if(newline)
            str.append('\n').append("  ".repeat(options.indent));
        str.append("</").append(tag).append('>');
    }

    /**
     * Returns the parent node of this node. May be null, if there is
     * no parent.
     *
     * @return The node's parent
     */
    @SuppressWarnings("NullableProblems")
    public Node getParent() {
        return parent;
    }

    /**
     * Returns the root node in this xml node tree.
     *
     * @return The root node of this node
     */
    public Node getRoot() {
        Node root = this;
        while(root.getParent() != null)
            root = root.getParent();
        return root;
    }

    /**
     * Returns the document this node is attached to, or <code>null</code>
     * if this node does not belong to a specific document.
     *
     * @return The document this node belongs to, or <code>null</code>
     */
    public Document getDocument() {
        Node root = getRoot();
        return root instanceof Document ? (Document) root : null;
    }

    /**
     * Sets this node's parent to the given node. The parent must not be
     * a child of this node.
     *
     * @param parent The node to set as parent
     */
    public void setParent(@Nullable Node parent) {
        if(parent != null)
            parent.children.add(this);
        else if(this.parent != null)
            this.parent.children.remove(this);
    }

    /**
     * Removes any blank {@link Text} nodes in this node's subtree.
     */
    public void removeBlankText() {
        removeBlankText0();
    }

    /**
     * Removes any blank text in this subtree.
     *
     * @return True if and only of this node was a blank text node and
     *         removed itself
     */
    boolean removeBlankText0() {
        for(int i=0; i<children.size(); i++)
            if(children.get(i).removeBlankText0()) i--;
        return false;
    }

    /**
     * Removes any {@link Comment} nodes from this subtree.
     */
    public void removeComments() {
        for(int i=0; i<children.size(); i++) {
            Node c = children.get(i);
            if(c instanceof Comment) children.remove(i--);
            else c.removeComments();
        }
    }

    /**
     * Returns this node as formatted xml string. This is identical to {@link #toXML()}.
     *
     * @return The outer xml string
     */
    @NotNull
    @Language("xml")
    public String outerXML() {
        return toXML();
    }

    /**
     * Returns this node as formatted html string. This is identical to {@link #toHTML()}.
     *
     * @return The outer html string
     */
    @NotNull
    @Language("html")
    public String outerHTML() {
        return toHTML();
    }

    /**
     * Returns this node as xml string. This is identical to {@link #toXML(long)}.
     *
     * @param options Formatting options, see {@link XML}
     * @return The outer xml string
     */
    @NotNull
    public String outerXML(long options) {
        return toXML(options);
    }

    /**
     * Returns this node's children as xml string. The string is not
     * formatted (indented, newlines).
     *
     * @return The inner xml string
     */
    @NotNull
    @Language("xml")
    public String innerXML() {
        return innerXML(XML.XML);
    }

    /**
     * Returns this node's children as html string. The string is not
     * formatted (indented, newlines).
     *
     * @return The inner html string
     */
    @NotNull
    @Language("html")
    public String innerHTML() {
        return innerXML(XML.HTML);
    }

    /**
     * Returns this node's children as xml string using the given formatting.
     *
     * @param options Formatting options, see {@link XML}
     * @return The inner xml string
     */
    @NotNull
    public String innerXML(long options) {
        StringBuilder str = new StringBuilder();
        innerXML(str, new FormattingOptions(options));
        return str.toString();
    }

    /**
     * Appends this node's inner xml string to the given StringBuilder using
     * the specified formatting.
     *
     * @param str The string builder to append to
     * @param options Formatting options
     */
    void innerXML(StringBuilder str, FormattingOptions options) {
        if(children.isEmpty()) return;

        for(int i=0; i<children.size(); i++) {
            if(i != 0 && options.formatted) str.append('\n').append("  ".repeat(options.indent));
            children.get(i).toString(str, options);
        }
    }


    /**
     * Sets this node's inner xml. This means all children will be removed and
     * instead the given string will be parsed as xml and the nodes will be added
     * as children.
     *
     * @param xml The xml to set as inner xml
     */
    public void setInnerXML(@NotNull @Language("xml") String xml) {
        setInnerXML(xml, XML.XML);
    }

    /**
     * Sets this node's inner html. This means all children will be removed and
     * instead the given string will be parsed as html and the nodes will be added
     * as children.
     *
     * @param html The xml to set as inner xml
     */
    public void setInnerHTML(@NotNull  @Language("html") String html) {
        setInnerXML(html, XML.HTML);
    }

    /**
     * Sets this node's inner xml. This means all children will be removed and
     * instead the given string will be parsed as xml and the nodes will be added
     * as children. If an exception occurs during parsing, the node will stay
     * unchanged.
     *
     * @param xml The xml to set as inner xml
     * @param options Parsing options, see {@link XML}
     */
    public void setInnerXML(@NotNull String xml, long options) {
        Arguments.checkNull(xml, "xml");
        List<Node> newChildren = new ArrayList<>();
        if(!xml.isBlank())
            for(Node node : XML.getParser(xml, options))
                newChildren.add(node);
        children.clear();
        children.addAll(newChildren);
    }

    /**
     * Replaces this node with the node parsed from the given xml string. The string may
     * contain exactly one root node. <b>This does not modify this node instance!</b> Instead,
     * a new node is returned, and unless this node is a root node, <b>this node gets replaced
     * in the parent's children list</b> with the new node. If an exception occurs during parsing,
     * the parent's children will not be changed.
     *
     * @param xml The xml string to use as outer xml
     * @return The new node
     */
    public Node withOuterXML(@NotNull @Language("xml") String xml) {
        return withOuterXML(xml, XML.XML);
    }

    /**
     * Replaces this node with the node parsed from the given html string. The string may
     * contain exactly one root node. <b>This does not modify this node instance!</b> Instead,
     * a new node is returned, and unless this node is a root node, <b>this node gets replaced
     * in the parent's children list</b> with the new node. If an exception occurs during parsing,
     * the parent's children will not be changed.
     *
     * @param html The html string to use as outer html
     * @return The new node
     */
    public Node withOuterHTML(@NotNull @Language("html") String html) {
        return withOuterXML(html, XML.HTML);
    }

    /**
     * Replaces this node with the node parsed from the given xml string. The string may
     * contain exactly one root node. <b>This does not modify this node instance!</b> Instead,
     * a new node is returned, and unless this node is a root node, <b>this node gets replaced
     * in the parent's children list</b> with the new node. If an exception occurs during parsing,
     * the parent's children will not be changed.
     *
     * @param xml The xml string to use as outer xml
     * @param options Parsing options, see {@link XML}
     * @return The new node
     */
    public Node withOuterXML(@NotNull String xml, long options) {
        Arguments.checkNull(xml, "xml");
        XMLParser parser = XML.getParser(xml, options);
        Node newNode = parser.next();
        if(parser.hasNext())
            throw new IllegalArgumentException("withOuterXML() xml string contains more than one root node");
        if(parent != null)
            parent.children.replace(this, newNode);
        return newNode;
    }

    /**
     * Sets this node to the node defined by the given xml string. The string may contain
     * exactly one root node. <b>The root node tag name must be identical</b> to this nodes
     * tag name! If you want to change the tag name of the node, use {@link #withOuterXML(String)}
     * instead which replaces this node with a new node. If an exception occurs during parsing,
     * this node will not be changed.
     *
     * @param xml The xml string to use as outer xml
     */
    public void setOuterXML(@NotNull @Language("xml") String xml) {
        setOuterXML(xml, XML.XML);
    }

    /**
     * Sets this node to the node defined by the given html string. The string may contain
     * exactly one root node. <b>The root node tag name must be identical</b> to this nodes
     * tag name! If you want to change the tag name of the node, use {@link #withOuterHTML(String)}}
     * instead which replaces this node with a new node. If an exception occurs during parsing,
     * this node will not be changed.
     *
     * @param html The html string to use as outer html
     */
    public void setOuterHTML(@NotNull @Language("html") String html) {
        setOuterXML(html, XML.HTML);
    }

    /**
     * Sets this node to the node defined by the given xml string. The string may contain
     * exactly one root node. <b>The root node tag name must be identical</b> to this nodes
     * tag name! If you want to change the tag name of the node, use {@link #withOuterXML(String,long)}
     * instead which replaces this node with a new node. If an exception occurs during parsing,
     * this node will not be changed.
     *
     * @param xml The xml string to use as outer xml
     * @param options Parsing options, see {@link XML}
     */
    public void setOuterXML(@NotNull String xml, long options) {
        Arguments.checkNull(xml, "xml");
        XMLParser parser = XML.getParser(xml, options);
        Node newNode = parser.next();
        if(parser.hasNext())
            throw new IllegalArgumentException("setOuterXML() xml string contains more than one root node");
        if(!tag.equals(newNode.tag))
            throw new IllegalArgumentException("setOuterXML() only allowed with the same root node tag");
        children.clear();
        attributes.clear();
        children.addAll(newNode.children); // Should not cause an exception - all nodes have been freshly parsed, should be unrelated instances
        attributes.putAll(newNode.attributes);
    }


    /**
     * Returns all text segments in this xml tree, joined with spaces.
     *
     * @return The text in this xml tree
     */
    @NotNull
    public String text() {
        return stream().ofType(Text.class).map(Text::text).filter(t -> !t.isEmpty()).collect(Collectors.joining(" "));
    }

    /**
     * Shorthand for {@code children.get(index)}.
     *
     * @param index The index of the child to get
     * @return The child at the given index
     */
    @NotNull
    public Node children(int index) {
        return child(index);
    }

    /**
     * Shorthand for {@code children.get(index)}.
     *
     * @param index The index of the child to get
     * @return The child at the given index
     */
    @NotNull
    public Node child(int index) {
        return children.get(index);
    }

    /**
     * Shorthand for {@code attributes.get(name)}.
     *
     * @param name The name of the attribute to get
     * @return The given attribute value, or null if the mapping doesn't exist
     */
    public String attribute(String name) {
        return attributes.get(name);
    }

    /**
     * Returns the <code>"id"</code> attribute, or an empty string if no
     * id attribute is present.
     *
     * @return The id attribute of this node
     */
    @NotNull
    public String id() {
        return attributes.getOrDefault("id", "");
    }

    /**
     * Sets the <code>"id"</code> attribute, or removes it if <code>null</code>
     * is passed.
     *
     * @param id The id to set, or <code>null</code>
     */
    public void setId(String id) {
        if(id != null)
            attributes.put("id", id);
        else attributes.remove("id");
    }

    /**
     * Returns the <code>"class"</code> attribute, or an empty string of no
     * class attribute is present.
     *
     * @return The class names of this node
     */
    @NotNull
    public String className() {
        return attributes.getOrDefault("class", "");
    }

    /**
     * Sets the <code>"class"</code> attribute, or removes it the given value is
     * <code>null</code> or a blank string.
     *
     * @param classNames The class names to set, or <code>null</code>
     */
    public void setClassName(String classNames) {
        if(classNames != null && !classNames.isBlank())
            attributes.put("class", classNames);
        else attributes.remove("class");
    }

    /**
     * Returns the value of the <code>"class"</code> attribute as set (splitting around
     * spaces) (the name is based on the JavaScript <code>classList</code> attribute name),
     * or an empty set if no class attribute is present.
     *
     * @return The class names of this node, as set
     */
    @NotNull
    public Set<String> classList() {
        String className = className().trim();
        if(className.isEmpty())
            return Set.of();
        return new HashSet<>(Arrays.asList(CLASS_SPLIT_PAT.split(className)));
    }

    /**
     * Sets the value of the <code>"class"</code> attribute to the given class names
     * joined with spaces, or removes the attribute if the list is <code>null</code>
     * or empty. The class names may not be empty, null, nor contain spaces.
     *
     * @param classList The class names to set, or <code>null</code>
     */
    public void setClassList(Collection<? extends String> classList) {
        if(classList == null || classList.isEmpty())
            attributes.remove("class");
        else {
            for(String cls : classList)
                if(cls.contains(" ") || cls.isEmpty())
                    throw new IllegalArgumentException("Class names may not be empty or contain spaces (got '"+cls+"')");
            setClassName(String.join(" ", classList));
        }
    }

    /**
     * Returns a stream over <b>this node and all its direct and indirect
     * sub-nodes</b>. If you want a stream over the direct children of this
     * node, use {@code children.stream()}.
     *
     * @return A stream over this xml tree
     */
    @NotNull
    public ListStream<Node> stream() {
        return ListStream.of(spliterator());
    }

    /**
     * Returns a stream over this xml subtree's elements, which are all of its
     * "normal" child nodes. This node is <b>not</b> included in the stream.
     *
     * @return A stream over the elements in this xml tree
     */
    @NotNull
    public ListStream<Node> getElements() {
        return stream().skip(1).filter(n -> n.getClass() == Node.class);
    }

    /**
     * Returns an iterator over <b>this node and all its direct and indirect
     * sub-nodes</b>. If you want an iterator over the direct children of this
     * node, use {@code children.iterator()}. The iterator traverses the tree
     * in depth-first traversal.
     *
     * @return A stream over this xml tree
     */
    @NotNull
    @Override
    public IterableIterator<Node> iterator() {
        return new IterableIterator<Node>() {
            Node next = Node.this;
            boolean nextReady = true;
            int i = -1;
            Iterator<Node> childIt = IterableIterator.empty();

            @Override
            public boolean hasNext() {
                updateNext();
                return next != null;
            }

            @Override
            public Node next() {
                updateNext();
                if(next == null) throw new NoSuchElementException();
                nextReady = false;
                return next;
            }

            void updateNext() {
                if(nextReady) return;
                nextReady = true;
                if(next == null) return;
                if(childIt.hasNext()) next = childIt.next();
                else {
                    while(i+1 < children.size() && !childIt.hasNext())
                        childIt = children(++i).iterator();
                    if(childIt.hasNext()) next = childIt.next();
                    else next = null;
                }
            }
        };
    }

    @Override
    public Spliterator<Node> spliterator() {
        if(children.isEmpty())
            return Arrays.spliterator(new Node[] { this });
        return Utils.spliterator(this, Long.MAX_VALUE, Spliterator.IMMUTABLE | Spliterator.ORDERED);
    }

    /**
     * Returns the first element in this xml tree with the given id attribute,
     * or null if none is found.
     *
     * @param id The id to search for
     * @return The node found, or null
     */
    public Node getElementById(@NotNull String id) {
        return getElementByAttr("id", id);
    }

    /**
     * Returns the first element in this xml tree with the given attribute value,
     * or null if none is found.
     *
     * @param attribute The attribute to check
     * @param value The value to search for
     * @return The node found, or null
     */
    public Node getElementByAttr(@NotNull String attribute, @NotNull String value) {
        return getElementsByAttr(attribute, value).findAny().orElse(null);
    }

    /**
     * Returns the first element in this xml tree with the given tag name, or
     * null of none is found.
     *
     * @param name The tag name to search for
     * @return The first node with that tag, or null
     */
    public Node getElementByTag(@NotNull String name) {
        return getElementsByTag(name).findFirst().orElse(null);
    }

    /**
     * Returns the first element with the given class attributes, or null
     * if none is found. The classes to search for have to be seperated by
     * space, order is irrelevant. The found object may have more classes.
     *
     * @param names The class names to search for, separated with spaces
     * @return The first node found, or null
     */
    public Node getElementByClass(@NotNull String names) {
        return getElementsByClass(names).findFirst().orElse(null);
    }

    /**
     * Returns the first element in this xml tree with the given name attribute,
     * or null if none is found.
     *
     * @param name The name to search for
     * @return The node found, or null
     */
    public Node getElementByName(@NotNull String name) {
        return getElementsByName(name).findFirst().orElse(null);
    }

    /**
     * Returns all elements in this xml tree with the given attribute value.
     *
     * @param attribute The attribute to check
     * @param value The value to search for
     * @return The nodes found
     */
    @NotNull
    public ListStream<Node> getElementsByAttr(@NotNull String attribute, @NotNull String value) {
        Arguments.checkNull(attribute);
        Arguments.checkNull(value);
        return getElements().filter(n -> value.equals(n.attribute(attribute)));
    }

    /**
     * Returns all elements in this xml tree with the given tag name.
     *
     * @param name The tag name to search for
     * @return The nodes found
     */
    @NotNull
    public ListStream<Node> getElementsByTag(@NotNull String name) {
        Arguments.checkNull(name);
        return getElements().filter(t -> name.equals(t.tag));
    }

    /**
     * Returns all elements with the given class attributes. The classes
     * to search for have to be seperated by space, order is irrelevant. The
     * found objects may have more classes.
     *
     * @param names The class names to search for, separated with spaces
     * @return The nodes found
     */
    @NotNull
    public ListStream<Node> getElementsByClass(@NotNull String names) {
        names = Arguments.checkNull(names, "names").trim();
        if(names.isEmpty()) return getElements();
        List<String> classes = Arrays.asList(CLASS_SPLIT_PAT.split(names.trim()));
        return getElements().filter(n -> n.classList().containsAll(classes));
    }

    /**
     * Returns all elements in this xml tree with the given name attribute.
     *
     * @param name The name to search for
     * @return The node found, or null
     */
    @NotNull
    public ListStream<Node> getElementsByName(@NotNull String name) {
        return getElementsByAttr("name", name);
    }

    /**
     * Collects the form data of this node and all its children, as a browser would when submitting a
     * <code>&lt;form></code> element. This node doesn't necessarily need to have the tag "form". If this
     * node has a value that would be collected in a form (e.g. it is an <code>&lt;input></code> element),
     * its value will be included in the form data.
     *
     * @return The form data within this node
     * @see FormData#collect(Node)
     */
    @NotNull
    public FormData formData() {
        return FormData.collect(this);
    }

    protected org.w3c.dom.Node asW3cNode() {
        return asW3cElement();
    }

    protected Element asW3cElement() {
        if(view == null)
            view = new W3cElementView(this);
        return view;
    }

    @Override
    public Object toJson() {
        JsonObject json = new JsonObject("tag", tag);
        if(!attributes.isEmpty())
            json.put("attributes", attributes);
        if(!children.isEmpty())
            json.put("children", children.stream().filter(c -> c.getClass() != Comment.class));
        return json;
    }
}
