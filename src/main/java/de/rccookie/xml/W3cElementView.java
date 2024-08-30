package de.rccookie.xml;

import de.rccookie.util.ViewModificationException;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

class W3cElementView extends AbstractW3cNodeView<de.rccookie.xml.Node> implements Element {


    W3cElementView(de.rccookie.xml.Node data) {
        super(data);
    }

    @Override
    public String getTagName() {
        return node.tag;
    }

    @NotNull
    @Override
    public String getAttribute(String name) {
        return node.attributes.getOrDefault(name, "");
    }

    @Override
    public void setAttribute(String name, String value) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public void removeAttribute(String name) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Attr getAttributeNode(String name) {
        return node.attributes.containsKey(name) ? new W3cAttrView(node, name) : null;
    }

    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        throw new ViewModificationException();
    }

    @NotNull
    @Override
    public NodeList getElementsByTagName(String name) {
        return new W3cNodeListView(name.equals("*") ? node.stream() : node.getElementsByTag(name));
    }

    @NotNull
    @Override
    public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
        return getAttribute(localName);
    }

    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
        return getAttributeNode(localName);
    }

    @Override
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        throw new ViewModificationException();
    }

    @NotNull
    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        return getElementsByTagName(localName);
    }

    @Override
    public boolean hasAttribute(String name) {
        return node.attributes.containsKey(name);
    }

    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        return hasAttribute(localName);
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return null;
    }

    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        throw new ViewModificationException();
    }

    @NotNull
    @Override
    public String getNodeName() {
        return getTagName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        return null;
    }

    @Override
    public short getNodeType() {
        return ELEMENT_NODE;
    }

    @Override
    public NamedNodeMap getAttributes() {
        return new W3cNodeMapView(node);
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public String getLocalName() {
        return getNodeName();
    }
}
