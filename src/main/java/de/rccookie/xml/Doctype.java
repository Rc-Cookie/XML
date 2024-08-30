package de.rccookie.xml;

import java.util.Collections;
import java.util.Objects;

import de.rccookie.json.Json;
import de.rccookie.json.JsonObject;
import de.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.DocumentType;

/**
 * A doctype node.
 */
public class Doctype extends Node {

    static {
        Json.registerDeserializer(Doctype.class, json -> {
            Doctype d = new Doctype(json.getString("rootElement"));
            d.setLocationType(json.get("locationType").as(LocationType.class));
            d.setName(json.getString("name"));
            d.setLocation(json.getString("location"));
            d.setStructure(json.getString("structure"));
            return d;
        });
    }

    /**
     * The root element attribute.
     */
    @NotNull
    private String rootElement;
    private LocationType locationType = null;
    private String name = null;
    private String location = null;
    private String structure = null;

    /**
     * Creates a new doctype node.
     *
     * @param rootElement The root element attribute value
     */
    public Doctype(String rootElement) {
        super("!DOCTYPE", AttributeMap.EMPTY, Collections.emptyList());
        this.rootElement = Arguments.checkNull(rootElement);
    }

    @Override
    public @NotNull Doctype clone() {
        Doctype copy = new Doctype(rootElement);
        copy.locationType = locationType;
        copy.name = name;
        copy.location = location;
        copy.structure = structure;
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Doctype)) return false;
        Doctype doctype = (Doctype) o;
        return rootElement.equals(doctype.rootElement) && locationType == doctype.locationType
                && Objects.equals(name, doctype.name) && Objects.equals(location, doctype.location)
                && Objects.equals(structure, doctype.structure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootElement, locationType, name, location, structure);
    }

    @Override
    public Object toJson() {
        JsonObject json = new JsonObject(
                "tag", "!DOCTYPE",
                "rootElement", rootElement
        );
        if(locationType != null)
            json.put("locationType", locationType);
        if(name != null)
            json.put("name", location);
        if(location != null)
            json.put("location", location);
        if(structure != null)
            json.put("structure", structure);
        return json;
    }

    @Override
    public Document getParent() {
        return (Document) super.getParent();
    }

    @Override
    public void setParent(@Nullable Node parent) {
        if(parent != null && !(parent instanceof Document))
            throw new IllegalArgumentException("Doctype can only be direct child of document");
        if(parent == null && this.parent != null)
            getParent().setDoctype(null);
        else if(parent != null)
            ((Document) parent).setDoctype(this);
    }

    public @NotNull String getRootElement() {
        return rootElement;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getStructure() {
        return structure;
    }

    public void setRootElement(@NotNull String rootElement) {
        this.rootElement = Arguments.checkNull(rootElement);
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    @Override
    void toString(StringBuilder str, FormattingOptions options) {
        str.append("<!DOCTYPE ").append(rootElement);
        if(locationType != null) {
            str.append(' ').append(locationType);
            if(locationType == LocationType.PUBLIC) str.append(" \"").append(name).append('"');
            str.append(" \"").append(location).append('"');
            if(locationType == LocationType.SYSTEM && structure != null)
                str.append(' ').append(structure);
        }
        else if(structure != null)
            str.append(' ').append(structure);
        str.append('>');
    }

    @Override
    protected DocumentType asW3cNode() {
        return new W3cDocumentTypeView(this);
    }


    public static Doctype defaultHtml() {
        // <!DOCTYPE html>
        return new Doctype("html");
    }

    public static Doctype defaultXhtml() {
        // <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
        Doctype doctype = new Doctype("html");
        doctype.setLocationType(LocationType.PUBLIC);
        doctype.setName("-//W3C//DTD XHTML 1.1//EN");
        doctype.setLocation("http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd");
        return doctype;
    }

    public static Doctype defaultSvg() {
        // <!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
        Doctype doctype = new Doctype("svg");
        doctype.setLocationType(LocationType.PUBLIC);
        doctype.setName("-//W3C//DTD SVG 1.1//EN");
        doctype.setLocation("http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd");
        return doctype;
    }

    public enum LocationType {
        SYSTEM,
        PUBLIC
    }
}
