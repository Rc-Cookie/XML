package de.rccookie.xml;

import de.rccookie.util.Arguments;
import de.rccookie.util.ViewModificationException;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

abstract class AbstractW3cNodeView<T extends de.rccookie.xml.Node> implements Node {

    final T node;

    AbstractW3cNodeView(T node) {
        this.node = Arguments.checkNull(node, "data");
    }

    @Override
    public String toString() {
        return node.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == getClass() && node.equals(((AbstractW3cNodeView<?>) obj).node);
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Node appendChild(Node newChild) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Node cloneNode(boolean deep) {
        throw new ViewModificationException();
    }

    @Override
    public void normalize() {
        throw new ViewModificationException();
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return false;
    }

    @Override
    public String getNamespaceURI() {
        return null;
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public String getBaseURI() {
        return null;
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public boolean isSameNode(Node other) {
        return this == other;
    }

    @Override
    public Node getParentNode() {
        de.rccookie.xml.Node parent = node.getParent();
        return parent != null ? parent.asW3cNode() : null;
    }

    @Override
    public boolean hasAttributes() {
        return !node.attributes.isEmpty();
    }

    @Override
    public boolean hasChildNodes() {
        return !node.children.isEmpty();
    }

    @NotNull
    @Override
    public NodeList getChildNodes() {
        return node.children.asW3cNodeList();
    }

    @Override
    public Node getFirstChild() {
        return node.children.isEmpty() ? null : node.children.get(0).asW3cNode();
    }

    @Override
    public Node getLastChild() {
        return node.children.isEmpty() ? null : node.children.get(node.children.size() - 1).asW3cNode();
    }

    @Override
    public Node getPreviousSibling() {
        de.rccookie.xml.Node parent = node.getParent();
        if(parent == null) return null;
        int index = parent.children.indexOf(node);
        if(index != 0) return parent.children.get(index - 1).asW3cNode();
        if(parent instanceof de.rccookie.xml.Document && ((de.rccookie.xml.Document) parent).getDoctype() != null)
            return ((de.rccookie.xml.Document) parent).getDoctype().asW3cNode();
        return null;
    }

    @Override
    public Node getNextSibling() {
        de.rccookie.xml.Node parent = node.getParent();
        if(parent == null) return null;
        int index = parent.children.indexOf(node);
        return index == parent.children.size()-1 ? null : parent.children.get(index + 1).asW3cNode();
    }

    @Override
    public Document getOwnerDocument() {
        de.rccookie.xml.Document document = node.getDocument();
        return document == null ? null : document.asW3cNode();
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        return null;
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return false;
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        return null;
    }

    @Override
    public boolean isEqualNode(Node arg) {
        return equals(arg);
    }

    @Override
    public Object getFeature(String feature, String version) {
        return null;
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        if(!(other instanceof AbstractW3cNodeView)) return DOCUMENT_POSITION_DISCONNECTED;

        if(other instanceof W3cDocumentView) {
            if(this instanceof W3cDocumentView) {
                return this.node == ((W3cDocumentView) other).node ?
                        DOCUMENT_POSITION_CONTAINS | DOCUMENT_POSITION_CONTAINED_BY | DOCUMENT_POSITION_PRECEDING | DOCUMENT_POSITION_FOLLOWING : DOCUMENT_POSITION_DISCONNECTED;
            }
            return getOwnerDocument() == other ? DOCUMENT_POSITION_CONTAINED_BY | DOCUMENT_POSITION_FOLLOWING : DOCUMENT_POSITION_DISCONNECTED;
        }
        if(this instanceof W3cDocumentView)
            return other.getOwnerDocument() == this ? DOCUMENT_POSITION_CONTAINS | DOCUMENT_POSITION_PRECEDING : DOCUMENT_POSITION_DISCONNECTED;

        de.rccookie.xml.Node o = ((AbstractW3cNodeView<?>) other).node;
        if(node == o)
            return DOCUMENT_POSITION_CONTAINS | DOCUMENT_POSITION_CONTAINED_BY | DOCUMENT_POSITION_PRECEDING | DOCUMENT_POSITION_FOLLOWING;
        if(node.getRoot() != o.getRoot())
            return DOCUMENT_POSITION_DISCONNECTED;
        if(node.stream().anyMatch(n -> n == o))
            return DOCUMENT_POSITION_CONTAINS | DOCUMENT_POSITION_PRECEDING;
        if(node.getRoot().stream().takeWhile(n -> n != node).anyMatch(n -> n == o)) {
            if(o.stream().anyMatch(n -> n == node))
                return DOCUMENT_POSITION_CONTAINED_BY | DOCUMENT_POSITION_FOLLOWING;
            return DOCUMENT_POSITION_FOLLOWING;
        }
        return DOCUMENT_POSITION_PRECEDING;
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        throw new ViewModificationException();
    }

    @Override
    public Object getUserData(String key) {
        return null;
    }

    @Override
    public String getTextContent() throws DOMException {
        return node.text();
    }
}
