package com.github.rccookie.xml;

import java.util.Collections;
import java.util.Objects;

import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * A doctype node.
 */
public class Doctype extends Node {

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

    public enum LocationType {
        SYSTEM,
        PUBLIC
    }
}
