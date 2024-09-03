package de.rccookie.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.rccookie.util.Arguments;
import de.rccookie.util.BoolWrapper;
import de.rccookie.util.RandomAccessListIterator;
import de.rccookie.util.RandomAccessSubList;
import de.rccookie.util.URLBuilder;
import org.jetbrains.annotations.NotNull;

public class FormData implements List<FormData.Entry> {

    private static final SortedSet<Integer> EMPTY_SET = new TreeSet<>();

    private final List<FormData.Entry> data = new ArrayList<>();
    private final Map<String, SortedSet<Integer>> lookup = new HashMap<>();

    public FormData() { }

    public FormData(List<Entry> data) {
        addAll(data);
    }

    public FormData(Entry... entries) {
        this(Arrays.asList(entries));
    }

    public FormData(Map<? extends String, ? extends String> entries) {
        addAll(entries);
    }

    @Override
    public String toString() {
        return stream().map(Entry::toString).collect(Collectors.joining("&"));
    }

    @Override
    public boolean equals(Object obj) {
        return data.equals(obj);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @NotNull
    public List<String> names() {
        return data.stream().map(Entry::name).collect(Collectors.toList());
    }

    @Override
    @NotNull
    public Entry get(int index) {
        return data.get(index);
    }

    public List<@NotNull Entry> getAllEntries(String name) {
        Arguments.checkNull(name, "name");
        return lookup.getOrDefault(name, EMPTY_SET).stream().map(data::get).collect(Collectors.toList());
//        return data.stream().filter(e -> e.name.equals(name)).collect(Collectors.toList());
    }

    public Entry getFirstEntry(String name) {
        Arguments.checkNull(name, "name");
        SortedSet<Integer> indices = lookup.get(name);
        return indices != null ? data.get(indices.first()) : null;
//        return data.stream().filter(e -> e.name.equals(name)).findFirst().orElse(null);
    }

    public List<String> getAll(String name) {
        Arguments.checkNull(name, "name");
        return lookup.getOrDefault(name, EMPTY_SET).stream().map(i -> data.get(i).value()).collect(Collectors.toList());
    }

    public String getFirst(String name) {
        Entry first = getFirstEntry(name);
        return first != null ? first.value() : null;
    }

    @Override
    @NotNull
    public Entry set(int index, @NotNull Entry element) {
        Entry removed = data.set(index, Arguments.checkNull(element, "element"));
        if(!element.name.equals(removed.name)) {
            addToLookup(element.name, index);
            removeFromLookup(removed.name, index);
        }
        return removed;
    }

    @NotNull
    public Entry set(int index, @NotNull String name, @NotNull String value) {
        return set(index, new Entry(name, value));
    }

    public Entry set(int index, String value) {
        return set(index, data.get(index).name, value);
    }

    public Entry put(Entry entry) {
        int index = indexOfName(Arguments.checkNull(entry, "entry").name);
        if(index >= 0)
            return set(index, entry);
        add(entry);
        return null;
    }

    public Entry put(String name, String value) {
        return put(new Entry(name, value));
    }

    public boolean putAll(Collection<? extends Entry> entries) {
        if(entries == this)
            return false;
        for(Entry e : entries)
            put(e);
        return !entries.isEmpty();
    }

    public boolean putAll(Map<? extends String, ? extends String> entries) {
        entries.forEach(this::put);
        return !entries.isEmpty();
    }

    @Override
    public void add(int index, @NotNull Entry element) {
        data.add(index, Arguments.checkNull(element, "element"));
        shiftLookup(index + 1, 1, false);
        addToLookup(element.name, index);
    }

    public void add(int index, String name, String value) {
        add(index, new Entry(name, value));
    }

    @Override
    @NotNull
    public Entry remove(int index) {
        Entry removed = data.remove(index);
        removeFromLookup(removed.name, index);
        shiftLookup(index, -1, false);
        return removed;
    }

    public Entry removeFirst(@NotNull String name) {
        int index = indexOfName(name);
        return index < 0 ? null : remove(index);
    }

    public boolean removeAll(@NotNull String name) {
        Arguments.checkNull(name, "name");
        if(!lookup.containsKey(name))
            return false;
        int removed = 0;
        for(int index : lookup.get(name).toArray(new Integer[0]))
            remove(index - (removed++));
        return true;
    }

    @Override
    public int indexOf(Object o) {
        return data.indexOf(o);
    }

    public int indexOfName(@NotNull String name) {
        SortedSet<Integer> indices = lookup.get(Arguments.checkNull(name, "name"));
        return indices != null ? indices.first() : -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return data.lastIndexOf(o);
    }

    public int lastIndexOf(String name) {
        SortedSet<Integer> indices = lookup.get(Arguments.checkNull(name, "name"));
        return indices != null ? indices.last() : -1;
    }

    @NotNull
    @Override
    public ListIterator<@NotNull Entry> listIterator() {
        return listIterator(0);
    }

    @NotNull
    @Override
    public ListIterator<@NotNull Entry> listIterator(int index) {
        return new RandomAccessListIterator<>(this, index);
    }

    @NotNull
    @Override
    public List<@NotNull Entry> subList(int fromIndex, int toIndex) {
        return RandomAccessSubList.ofRange(this, fromIndex, toIndex);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return data.contains(o);
    }

    public boolean containsName(@NotNull String name) {
        return lookup.containsKey(Arguments.checkNull(name, "name"));
    }

    @NotNull
    @Override
    public Iterator<@NotNull Entry> iterator() {
        return listIterator();
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return data.toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        return data.toArray(a);
    }

    @Override
    public boolean add(@NotNull Entry entry) {
        data.add(Arguments.checkNull(entry, "entry"));
        addToLookup(entry.name, data.size() - 1);
        return true;
    }

    public boolean add(@NotNull String name, @NotNull String value) {
        return add(new Entry(name, value));
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if(index < 0)
            return false;
        remove(index);
        return true;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        //noinspection SuspiciousMethodCalls
        return data.contains(c);
    }

    public boolean containsAllNames(@NotNull Collection<? extends @NotNull String> names) {
        return lookup.keySet().containsAll(names);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends @NotNull Entry> c) {
        for(Entry e : Arguments.checkNull(c, "c"))
            Arguments.checkNull(e, "element of c");
        for(Entry e : c)
            add(e);
        return !c.isEmpty();
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends @NotNull Entry> c) {
        for(Entry e : Arguments.checkNull(c, "c"))
            Arguments.checkNull(e, "element of c");

        if(!data.addAll(index, c))
            return false;

        shiftLookup(index + c.size(), c.size(), false);
        for(Entry e : c)
            addToLookup(e.name, index++);
        return true;
    }

    public boolean addAll(@NotNull Map<? extends String, ? extends String> entries) {
        BoolWrapper any = new BoolWrapper(false);
        entries.forEach((n,v) -> any.value |= add(n,v));
        return any.value;
    }

    public boolean addAll(int index, Map<? extends String, ? extends String> entries) {
        return addAll(index, new FormData(entries));
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        if(!data.removeAll(Arguments.checkNull(c, "c")))
            return false;
        rebuildLookup();
        return true;
    }

    public boolean removeAllNames(@NotNull Collection<? extends String> names) {
        for(String n : Arguments.checkNull(names, "names"))
            Arguments.checkNull(n, "element of names");

        boolean any = false;
        for(String n : names)
            any |= removeAll(n);
        return any;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        if(!data.retainAll(Arguments.checkNull(c, "c")))
            return false;
        rebuildLookup();
        return true;
    }

    public boolean retainAllNames(@NotNull Collection<? extends String> names) {
        Set<String> namesSet = new HashSet<>(names);
        return removeIf(e -> !namesSet.contains(e.name));
    }

    @Override
    public boolean removeIf(Predicate<? super Entry> filter) {
        if(!data.removeIf(filter))
            return false;
        rebuildLookup();
        return true;
    }

    @Override
    public void clear() {
        data.clear();
    }

    public Map<String, String> toMap() {
        return data.stream().collect(Collectors.toMap(Entry::name, Entry::value, (a,b) -> { throw new IllegalStateException("FormData contains multiple entries with the same name, cannot convert to map"); }, LinkedHashMap::new));
    }

    public Map<String, List<String>> toMultiMap() {
        return data.stream().collect(Collectors.toMap(Entry::name, e -> new ArrayList<>(List.of(e.value())), (a,b) -> { a.addAll(b); return a; }));
    }

    public Map<String, Entry> toEntryMap() {
        return data.stream().collect(Collectors.toMap(Entry::name, e -> e, (a,b) -> { throw new IllegalStateException("FormData contains multiple entries with name "+a.name+", cannot convert to map"); }, LinkedHashMap::new));
    }

    public Map<String, List<Entry>> toMultiEntryMap() {
        return data.stream().collect(Collectors.toMap(Entry::name, e -> new ArrayList<>(List.of(e)), (a,b) -> { a.addAll(b); return a; }));
    }




    private void addToLookup(String name, int index) {
        lookup.computeIfAbsent(name, n -> new TreeSet<>()).add(index);
    }

    private void removeFromLookup(String name, int index) {
        Set<Integer> indices = lookup.get(name);
        if(indices == null)
            return;
        // In case indices ever gets changed to a list
        //noinspection RedundantCast
        indices.remove((Object) index);
        if(indices.isEmpty())
            lookup.remove(name);
    }

    private void shiftLookup(int fromIndex, int shift, boolean currentlyCorrect) {
        if(shift > 0) {
            for(int i=data.size()-1; i>=fromIndex; i--)
                doShift(i, shift, currentlyCorrect);
        }
        else {
            for(int i=fromIndex; i<data.size(); i++)
                doShift(i, shift, currentlyCorrect);
        }
    }

    private void doShift(int index, int shift, boolean currentlyCorrect) {
        Set<Integer> indices = lookup.get(data.get(index).name);
        if(currentlyCorrect) {
            indices.remove(index);
            indices.add(index + shift);
        }
        else {
            indices.remove(index - shift);
            indices.add(index);
        }
    }

    private void rebuildLookup() {
        lookup.clear();
        for(int i=0; i<data.size(); i++)
            addToLookup(data.get(i).name, i);
    }




    private static final Set<String> FORM_DATA_SRC_TAGS = Set.of("input", "select", "textarea");

    public static FormData collect(Node form) {
        FormData data = new FormData();

        for(Node n : form) {
            if(!FORM_DATA_SRC_TAGS.contains(n.tag.toLowerCase()) || n.attributes.containsKey("disabled") || !n.attributes.containsKey("name"))
                continue;

            String value;
            if(n.tag.equalsIgnoreCase("input")) {

                String type = n.attributes.getOrDefault("type", "text").toLowerCase();
                if(type.equals("button") || type.equals("input") || type.equals("reset") || type.equals("submit"))
                    continue;

                if((type.equals("checkbox") || type.equals("radio")) && !n.attributes.containsKey("checked"))
                    continue;

                if(type.equals("file") && !n.attributes.containsKey("value"))
                    continue;

                value = n.attributes.getOrDefault("value", "");
            }
            else if(n.tag.equalsIgnoreCase("select")) {
                List<Node> options = n.getElementsByTag("option").toList();
                if(options.isEmpty())
                    continue;
                List<Node> selected = options.stream().filter(o -> o.attributes.containsKey("selected")).collect(Collectors.toList());
                if(n.attributes.containsKey("multiple")) {
                    String name = n.attribute("name");
                    for(Node s : selected)
                        data.add(name, s.attributes.getOrDefault("value", ""));
                    continue;
                }
                Node onlySelected = selected.isEmpty() ? options.get(0) : selected.get(selected.size() - 1);
                value = onlySelected.attributes.getOrDefault("value", "");
            }
            else {
                value = n.innerHTML().trim();
                if(!n.children.isEmpty() && !(n.child(n.children.size() - 1) instanceof Text))
                    // Ends with closing tag
                    value += "\r\n";
            }

            data.add(n.attribute("name"), value);
        }
        return data;
    }

    public static final class Entry {

        private final String name;

        private final String value;
        private final byte[] rawValue;

        public Entry(String name, byte[] rawValue) {
            this.name = Arguments.checkNull(name, "name");
            this.value = null;
            this.rawValue = Arguments.checkNull(rawValue, "value");
        }

        public Entry(String name, String value) {
            this.name = Arguments.checkNull(name, "name");
            this.value = Arguments.checkNull(value, "value");
            this.rawValue = null;
        }

        @Override
        public String toString() {
            return URLBuilder.queryString(java.util.Map.of(name, value()));
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(!(o instanceof Entry)) return false;
            Entry entry = (Entry) o;
            return name.equals(entry.name) && (value != null && entry.value != null ? value.equals(entry.value) : Arrays.equals(rawValue(), entry.rawValue()));
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(name);
            result = 31 * result + Arrays.hashCode(rawValue());
            return result;
        }

        @NotNull
        public String name() {
            return name;
        }

        public byte @NotNull [] rawValue() {
            return rawValue != null ? rawValue : value.getBytes();
        }

        @NotNull
        public String value() {
            return value != null ? value : new String(rawValue);
        }
    }
}
