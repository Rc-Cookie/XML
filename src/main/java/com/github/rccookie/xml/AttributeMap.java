package com.github.rccookie.xml;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.github.rccookie.util.Arguments;

public class AttributeMap implements Map<String,String> {

    public static final AttributeMap EMPTY = new AttributeMap() {
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
        void toString(StringBuilder out) {
        }
    };

    private final Map<String,String> map = new LinkedHashMap<>();

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
        toString(str);
        return str.toString();
    }

    void toString(StringBuilder out) {
        forEach((k,v) -> {
            out.append(' ');
            XMLEncoder.encode(k, out);
            out.append("=\"");
            XMLEncoder.encode(v, out);
            out.append('"');
        });
    }

    private static String check(String notNull) {
        return Arguments.checkNull(notNull, "No null keys and values allowed");
    }
}
