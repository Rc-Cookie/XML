package com.github.rccookie.xml;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Cloneable;
import com.github.rccookie.util.IterableIterator;
import com.github.rccookie.util.ListStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a node in an xml tree. A node has a tag, attributes and children.
 * It also holds a reference to its parent. Nodes also describe the text sections
 * in the document.
 */
public class Node implements Iterable<Node>, Cloneable<Node> {

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


    /**
     * Creates a new node with the given tag name.
     *
     * @param tag The tag name for the node
     */
    public Node(@NotNull String tag) {
        this(tag, null, null);
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

        str.append('<').append(tag);
        attributes.toString(str);

        if(children.isEmpty()) {
            if(options.html && XMLParser.HTML_VOID_TAGS.contains(tag)) {
                str.append('>');
                return;
            }
            else if(options.collapseEmpty) {
                str.append("/>");
                return;
            }
        }

        str.append('>');
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
     * Returns this node's children as xml string. The string is not
     * formatted (indented, newlines).
     *
     * @return The inner xml string
     */
    @NotNull
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
    public void setInnerXML(@NotNull String xml) {
        setInnerXML(xml, XML.XML);
    }

    /**
     * Sets this node's inner html. This means all children will be removed and
     * instead the given string will be parsed as html and the nodes will be added
     * as children.
     *
     * @param html The xml to set as inner xml
     */
    public void setInnerHTML(@NotNull String html) {
        setInnerXML(html, XML.HTML);
    }

    /**
     * Sets this node's inner xml. This means all children will be removed and
     * instead the given string will be parsed as xml and the nodes will be added
     * as children.
     *
     * @param xml The xml to set as inner xml
     * @param options Parsing options, see {@link XML}
     */
    public void setInnerXML(@NotNull String xml, long options) {
        Arguments.checkNull(xml, "xml");
        children.clear();
        for(Node node : XML.getParser(xml))
            children.add(node);
    }

    /**
     * Returns all text segments in this xml tree, joined with spaces.
     *
     * @return The text in this xml tree
     */
    @NotNull
    public String text() {
        return stream().filterType(Text.class).map(Text::text).filter(t -> !t.isEmpty()).collect(Collectors.joining(" "));
    }

    /**
     * Shorthand for {@code children.get(index)}, to shorten chained calls.
     *
     * @param index The index of the child to get
     * @return The child at the given index
     */
    @NotNull
    public Node children(int index) {
        return children.get(index);
    }

    /**
     * Shorthand for {@code attributes.get(name)}, to shorten chained calls.
     *
     * @param name The name of the attribute to get
     * @return The given attribute value, or null if the mapping doesn't exist
     */
    public String attribute(String name) {
        return attributes.get(name);
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
        return ListStream.of((Iterator<Node>) iterator());
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
        String[] classes = names.split("\\s+");
        return getElements().filter(n -> {
            String cs = n.attribute("class");
            for(String c : classes)
                if(!cs.contains(c)) return false;
            return true;
        });
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
}
