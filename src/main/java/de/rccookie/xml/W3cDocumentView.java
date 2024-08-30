package de.rccookie.xml;

import java.util.ArrayList;
import java.util.List;

import de.rccookie.util.ViewModificationException;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

final class W3cDocumentView extends AbstractW3cNodeView<de.rccookie.xml.Document> implements Document {

    W3cDocumentView(de.rccookie.xml.Document node) {
        super(node);
    }

    @Override
    public DocumentType getDoctype() {
        return node.getDoctype() != null ? node.getDoctype().asW3cNode() : null;
    }

    @Override
    public DOMImplementation getImplementation() {
        return W3cImplementation.INSTANCE;
    }

    @Override
    public Element getDocumentElement() {
        de.rccookie.xml.Node child = node.children.get(0);
        return child != null ? child.asW3cElement() : null;
    }

    @Override
    public Element createElement(String tagName) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        throw new ViewModificationException();
    }

    @Override
    public Text createTextNode(String data) {
        throw new ViewModificationException();
    }

    @Override
    public Comment createComment(String data) {
        throw new ViewModificationException();
    }

    @Override
    public CDATASection createCDATASection(String data) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Attr createAttribute(String name) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public EntityReference createEntityReference(String name) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public NodeList getElementsByTagName(String tagname) {
        return new W3cNodeListView(tagname.equals("*") ? node.stream() : node.getElementsByTag(tagname));
    }

    @Override
    public Node importNode(Node importedNode, boolean deep) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return getElementsByTagName(localName);
    }

    @Override
    public Element getElementById(String elementId) {
        return node.getElementById(elementId).asW3cElement();
    }

    @Override
    public String getInputEncoding() {
        return null;
    }

    @Override
    public String getXmlEncoding() {
        return null;
    }

    @Override
    public boolean getXmlStandalone() {
        XMLDeclaration declaration = node.getXMLDeclaration();
        return declaration != null && declaration.isStandalone();
    }

    @Override
    public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public String getXmlVersion() {
        XMLDeclaration declaration = node.getXMLDeclaration();
        return declaration != null ? declaration.getVersion() : "1.0";
    }

    @Override
    public void setXmlVersion(String xmlVersion) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public boolean getStrictErrorChecking() {
        return true;
    }

    @Override
    public void setStrictErrorChecking(boolean strictErrorChecking) {
        throw new ViewModificationException();
    }

    @Override
    public String getDocumentURI() {
        return null;
    }

    @Override
    public void setDocumentURI(String documentURI) {
        throw new ViewModificationException();
    }

    @Override
    public Node adoptNode(Node source) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public DOMConfiguration getDomConfig() {
        return null;
    }

    @Override
    public void normalizeDocument() {
        throw new ViewModificationException();
    }

    @Override
    public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
        throw new ViewModificationException();
    }

    @NotNull
    @Override
    public String getNodeName() {
        return "#document";
    }

    @Override
    public String getNodeValue() throws DOMException {
        return null;
    }

    @Override
    public short getNodeType() {
        return DOCUMENT_NODE;
    }

    @Override
    public Node getParentNode() {
        return null;
    }

    @NotNull
    @Override
    public NodeList getChildNodes() {
        List<de.rccookie.xml.Node> childNodes = new ArrayList<>();
        if(node.getDoctype() != null)
            childNodes.add(node.getDoctype());
        childNodes.addAll(node.children);
        return new W3cNodeListView(childNodes);
    }

    @Override
    public Node getFirstChild() {
        if(node.getDoctype() != null)
            return node.getDoctype().asW3cNode();
        return node.children.isEmpty() ? null : node.children.get(0).asW3cNode();
    }

    @Override
    public Node getLastChild() {
        if(!node.children.isEmpty())
            return node.children.get(node.children.size() - 1).asW3cNode();
        return node.getDoctype() != null ? node.getDoctype().asW3cNode() : null;
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
        return null; // yes, this is the specification
    }

    @Override
    public boolean hasChildNodes() {
        return node.getXMLDeclaration() != null || node.getDoctype() != null || !node.children.isEmpty();
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public String getLocalName() {
        return null;
    }
}
