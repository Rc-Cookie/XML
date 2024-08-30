package de.rccookie.xml;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

final class W3cDocumentTypeView extends AbstractW3cNodeView<Doctype> implements DocumentType {

    W3cDocumentTypeView(Doctype node) {
        super(node);
    }

    @Override
    public String getName() {
        return node.getRootElement();
    }

    @Override
    public NamedNodeMap getEntities() {
        return W3cNodeMapView.EMPTY;
    }

    @Override
    public NamedNodeMap getNotations() {
        return W3cNodeMapView.EMPTY;
    }

    @Override
    public String getPublicId() {
        return node.getLocationType() == Doctype.LocationType.PUBLIC ? node.getName() : null;
    }

    @Override
    public String getSystemId() {
        return node.getLocationType() == Doctype.LocationType.SYSTEM ? node.getLocation() : null;
    }

    @Override
    public String getInternalSubset() {
        return null;
    }

    @NotNull
    @Override
    public String getNodeName() {
        return getName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        return null;
    }

    @Override
    public short getNodeType() {
        return DOCUMENT_TYPE_NODE;
    }

    @Override
    public Node getNextSibling() {
        return getOwnerDocument().getDocumentElement();
    }

    @Override
    public NamedNodeMap getAttributes() {
        return null;
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
