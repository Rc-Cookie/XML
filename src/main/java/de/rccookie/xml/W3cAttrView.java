package de.rccookie.xml;

import java.util.Objects;

import de.rccookie.util.Arguments;
import de.rccookie.util.ViewModificationException;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

final class W3cAttrView implements Attr {

    final de.rccookie.xml.Node node;
    private final String name, value;

    public W3cAttrView(de.rccookie.xml.Node node, String name) {
        this.node = Arguments.checkNull(node, "node");
        this.name = Arguments.checkNull(name, "name");
        this.value = Objects.requireNonNull(node.attribute(name));
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof W3cAttrView && name.equals(((W3cAttrView) obj).name) && value.equals(((W3cAttrView) obj).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean getSpecified() {
        return true;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Element getOwnerElement() {
        return node.asW3cElement();
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }

    @Override
    public boolean isId() {
        return name.equals("id");
    }

    @NotNull
    @Override
    public String getNodeName() {
        return getName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        return getValue();
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public short getNodeType() {
        return Node.ATTRIBUTE_NODE;
    }

    @Override
    public Node getParentNode() {
        return null;
    }

    @NotNull
    @Override
    public NodeList getChildNodes() {
        return W3cNodeListView.EMPTY;
    }

    @Override
    public Node getFirstChild() {
        return null;
    }

    @Override
    public Node getLastChild() {
        return null;
    }

    @Override
    public Node getPreviousSibling() {
        return null;
    }

    @Override
    public Node getNextSibling() {
        return null;
    }

    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }

    @Override
    public Document getOwnerDocument() {
        de.rccookie.xml.Document document = node.getDocument();
        return document != null ? document.asW3cNode() : null;
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
    public boolean hasChildNodes() {
        return false;
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
    public String getPrefix() {
        return null;
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public String getLocalName() {
        return name;
    }

    @Override
    public boolean hasAttributes() {
        return false;
    }

    @Override
    public String getBaseURI() {
        return null;
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        if(!(other instanceof W3cAttrView)) return DOCUMENT_POSITION_DISCONNECTED;

        W3cAttrView attr = (W3cAttrView) other;
        if(node == attr.node) {
            return (short) (DOCUMENT_POSITION_CONTAINS | DOCUMENT_POSITION_CONTAINED_BY | DOCUMENT_POSITION_PRECEDING | DOCUMENT_POSITION_FOLLOWING |
                    (name.equals(attr.name) ? 0 : DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC));
        }
        if(node.getRoot() != attr.node.getRoot())
            return DOCUMENT_POSITION_DISCONNECTED;
        if(node.getRoot().stream().takeWhile(n -> n != node).anyMatch(n -> n == attr.node))
            return DOCUMENT_POSITION_FOLLOWING;
        return DOCUMENT_POSITION_PRECEDING;
    }

    @Override
    public String getTextContent() throws DOMException {
        return value;
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public boolean isSameNode(Node other) {
        return other instanceof W3cAttrView && node == ((W3cAttrView) other).node && name.equals(((W3cAttrView) other).name);
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
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        throw new ViewModificationException();
    }

    @Override
    public Object getUserData(String key) {
        return null;
    }
}
