package com.github.rccookie.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.ListStream;

import org.jetbrains.annotations.NotNull;

/**
 * A list that describes the children of a node. Duplicate entries are
 * not permitted, and will just be moved when trying to add them again.
 */
// Checks no cycle references, sets parent value
public class NodeList implements List<Node> {

    /**
     * The list that backs this node list.
     */
    @NotNull
    private final List<Node> list;
    /**
     * The node that this children list belongs to.
     */
    @NotNull
    private final Node node;

    /**
     * Creates a new, empty node list for the given node.
     *
     * @param node The node that the list should belong to
     */
    NodeList(@NotNull Node node) {
        this(new ArrayList<>(), node);
    }

    /**
     * Creates a new node list using the given list as backing for
     * the node list.
     *
     * @param list The list to use as backing for this list
     * @param node The node that this list belongs to
     */
    NodeList(@NotNull List<Node> list, @NotNull Node node) {
        this.list = Arguments.checkNull(list, "list");
        this.node = Arguments.checkNull(node, "node");
        for(Node n : list) checkNotParent(Arguments.checkNull(n, "child node"));
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<Node> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T @NotNull [] a) {
        //noinspection SuspiciousToArrayCall
        return list.toArray(a);
    }

    @Override
    public boolean add(Node element) {
        return add0(list.size(), element);
    }

    private static void checkNotParent(Node node) {
        for(Node p = node.parent; p != null; p = p.parent)
            if(p == node) throw new NestedXMLNodeException();
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if(index == -1) return false;
        remove(index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object o : c)
            if(!contains(o)) return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Node> c) {
        boolean changed = false;
        for(Node n : c) changed |= add(n);
        return changed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Node> c) {
        boolean changed = false;
        for(Node n : c) {
            if(add0(index, n)) {
                changed = true;
                index++;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for(Object o : c) changed |= remove(o);
        return changed;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean changed = false;
        for(int i=0; i<list.size(); i++) {
            if(!c.contains(list.get(i))) {
                remove(i--);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Node get(int index) {
        return list.get(index);
    }

    @Override
    public Node set(int index, Node element) {
        checkNotParent(element);
        Node prev = remove(index);
        add(index, element);
        return prev;
    }

    @Override
    public void add(int index, Node element) {
        add0(index, element);
    }

    /**
     * Adds the given node to this list. It first checks whether the given node
     * would create a cycle reference. Then it tests whether the node is already
     * in this list. If it is, it will be moved to the specified index. Otherwise,
     * the node gets added at the given index.
     *
     * @param index The index to add or move the node to
     * @param element The node to add
     * @return False if the node was already in the list at that index, true otherwise
     */
    private boolean add0(int index, Node element) {
        checkNotParent(element);
        int prevIndex = indexOf(element);
        if(prevIndex != -1) {
            if(prevIndex == index) return false;
            list.remove(prevIndex);
            list.add(index, element);
        }
        else {
            if(element.parent != null)
                element.parent.children.remove(element);
            list.add(index, element);
            element.parent = this.node;
        }
        return true;
    }

    @Override
    public Node remove(int index) {
        Node prev = list.remove(index);
        prev.parent = null;
        return prev;
    }

    /**
     * Returns the index of the given node in this list. This method searches
     * for the given <b>instance</b>, not for a node equal to it.
     *
     * @param o The node to search for
     * @return The index of the node, or -1 if not found
     */
    @Override
    public int indexOf(Object o) {
        for(int i=0, stop=list.size(); i<stop; i++)
            if(list.get(i) == o) return i;
        return -1;
    }

    /**
     * Returns the index of the given node in this list, searching from the back.
     * This method searches for the given <b>instance</b>, not for a node equal
     * to it.
     *
     * @param o The node to search for
     * @return The index of the node, or -1 if not found
     */
    @Override
    public int lastIndexOf(Object o) {
        for(int i=list.size()-1; i>=0; i--)
            if(list.get(i) == o) return i;
        return -1;
    }

    @Override
    public ListIterator<Node> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<Node> listIterator(int index) {
        ListIterator<Node> it = list.listIterator(index);
        return new ListIterator<>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Node next() {
                return it.next();
            }

            @Override
            public boolean hasPrevious() {
                return it.hasPrevious();
            }

            @Override
            public Node previous() {
                return it.previous();
            }

            @Override
            public int nextIndex() {
                return it.nextIndex();
            }

            @Override
            public int previousIndex() {
                return it.previousIndex();
            }

            @Override
            public void remove() {
                it.remove();
            }

            @Override
            public void set(Node node) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Node node) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public List<Node> subList(int fromIndex, int toIndex) {
        return new NodeList(list.subList(fromIndex, toIndex), node);
    }


    @Override
    public boolean equals(Object o) {
        return list.equals(o);
    }

    @Override
    public int hashCode() {
        return list.hashCode(); // Don't use node to avoid cycle reference
    }

    @Override
    public String toString() {
        return list.toString();
    }

    @Override
    public ListStream<Node> stream() {
        return ListStream.of(this);
    }

    @Override
    public Stream<Node> parallelStream() {
        return stream();
    }
}
