package com.mtecresults.ranking;

import java.util.HashSet;
import java.util.Set;

public class Node<T extends Comparable<? super T>> implements Comparable<T> {
    //set of all keys where A.compareTo(B) == 0, but !A.equals(B)
    //this set must ALWAYS BE NON-EMPTY - any removal from the set that would
    //make it empty must result in the removal of this Node
    private Set<T> keys;

    Node<T> parent;
    Node<T> left;
    Node<T> right;

    int height;
    int count;

    Node(final T key) {
        this.keys = new HashSet<>();
        this.keys.add(key);
    }

    @Override
    public int compareTo(T o) {
        return -1 * o.compareTo(keys.iterator().next());
    }

    public boolean contains(T element){
        return keys.contains(element);
    }

    //element.compareTo(A), A any member of keys must == 0
    public void add(final T element){
        keys.add(element);
    }

    public boolean remove(final T element){
        return keys.remove(element);
    }

    public int size() {
        return keys.size();
    }

    public Set<T> getKeys() {
        //return a copy
        return new HashSet<>(keys);
    }

    public void setKeys(final Set<T> keys){
        this.keys = keys;
    }
}