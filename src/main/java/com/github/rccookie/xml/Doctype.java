package com.github.rccookie.xml;

import java.util.Collections;
import java.util.Objects;

public class Doctype extends Node {

    private String rootElement;
    private LocationType locationType = null;
    private String name = null;
    private String location = null;
    private String structure = null;

    public Doctype(String rootElement) {
        super("!DOCTYPE", Collections.emptyMap(), Collections.emptyList());
        this.rootElement = Objects.requireNonNull(rootElement);
    }

    public String getRootElement() {
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

    public void setRootElement(String rootElement) {
        this.rootElement = Objects.requireNonNull(rootElement);
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
    void toString(StringBuilder str) {
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
    public void setInnerXML(String xml) {
        super.setInnerXML(xml);
    }

    public enum LocationType {
        SYSTEM,
        PUBLIC
    }
}
