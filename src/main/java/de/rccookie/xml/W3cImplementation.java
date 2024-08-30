package de.rccookie.xml;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

final class W3cImplementation implements DOMImplementation {

    public static final W3cImplementation INSTANCE = new W3cImplementation();

    private W3cImplementation() { }

    @Override
    public boolean hasFeature(String feature, String version) {
        return false;
    }

    @Override
    public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getFeature(String feature, String version) {
        return null;
    }
}
