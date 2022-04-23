package com.github.rccookie.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

// Checks no cycle references, sets parent value
class NodeList implements List<Node> {

    private final List<Node> list;
    private final Node node;

    NodeList(Node node) {
        this(new ArrayList<>(), node);
    }

    NodeList(List<Node> list, Node node) {
        this.list = list;
        this.node = node;
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
    public <T> T[] toArray(T[] a) {
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
    public boolean retainAll(Collection<?> c) {
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

    @Override
    public int indexOf(Object o) {
        for(int i=0, stop=list.size(); i<stop; i++)
            if(list.get(i) == o) return i;
        return -1;
    }

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
}
