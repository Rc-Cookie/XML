package de.rccookie.xml;

import org.jetbrains.annotations.NotNull;

public interface XMLSerializable {

    @NotNull
    Node toXML();
}
