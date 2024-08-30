package de.rccookie.xml;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import de.rccookie.json.Json;
import de.rccookie.json.JsonObject;
import de.rccookie.json.JsonSerializable;
import de.rccookie.util.Arguments;
import de.rccookie.util.Cloneable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A map that does not allow null keys and values.
 */
public class AttributeMap implements Map<String,String>, Cloneable<AttributeMap>, JsonSerializable {

    static {
        Json.registerDeserializer(AttributeMap.class, json -> {
            AttributeMap map = new AttributeMap();
            map.putAll(json.asMap(String.class));
            return map;
        });
    }

    /**
     * Singleton empty map.
     */
    static final AttributeMap EMPTY = new AttributeMap() {
        @Override
        public @NotNull AttributeMap clone() {
            return this;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public String get(Object key) {
            return null;
        }

        @Override
        public String put(String key, String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends String, ? extends String> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
        }

        @Override
        public Set<String> keySet() {
            return Set.of();
        }

        @Override
        public Collection<String> values() {
            return Set.of();
        }

        @Override
        public Set<Entry<String, String>> entrySet() {
            return Set.of();
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public String toString() {
            return "";
        }

        @Override
        void toString(StringBuilder out, FormattingOptions options) {
        }
    };

    /**
     * The map backing this attribute map.
     */
    private final Map<String,String> map = new LinkedHashMap<>();

    /**
     * Creates a new attribute map.
     */
    // package-private
    AttributeMap() {
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return map.get(key);
    }

    @Override
    public String put(String key, String value) {
        if(key.contains(" ") || key.contains("="))
            throw new XMLParseException("' ' and '=' not allowed in XML attribute keys");
        return map.put(check(key), check(value));
    }

    @Override
    public String remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<String> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return map.entrySet();
    }


    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        toString(str, new FormattingOptions(XML.XML));
        return str.toString();
    }

    /**
     * Appends these attributes to the given StringBuilder. A leading space
     * will also be included.
     *
     * @param out The string builder to write into
     */
    void toString(StringBuilder out, FormattingOptions options) {
        forEach((k,v) -> {
            out.append(' ');
            if(options.xhtml) {
                String lower = k.toLowerCase();
                if(!options.tryFixErrors && !lower.equals(k))
                    throw new IllegalStateException("Uppercase attribute keys not allowed in XHTML, found '"+k+"'");
                k = lower;
            }
            XMLEncoder.encode(k, out);
            out.append("=\"");
            XMLEncoder.encode(v, out);
            out.append('"');
        });
    }

    /**
     * Checks that the given string is not null.
     *
     * @param notNull The string to check
     * @return The input string
     */
    @Contract("null->fail;_->param1")
    private static String check(String notNull) {
        return Arguments.checkNull(notNull, "No null keys and values allowed");
    }

    /**
     * Creates a copy of this attribute map.
     *
     * @return A copy of this map
     */
    @Override
    public @NotNull AttributeMap clone() {
        AttributeMap copy = new AttributeMap();
        copy.map.putAll(map);
        return copy;
    }

    @Override
    public Object toJson() {
        return new JsonObject(map);
    }
}
