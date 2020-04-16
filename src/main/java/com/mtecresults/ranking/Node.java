package com.mtecresults.ranking;

import java.util.HashSet;
import java.util.Set;

public class Node<T extends Comparable<? super T>> implements Comparable<T> {
    //set of all keys where A.compareTo(B) == 0, but !A.equals(B)
    //this set must ALWAYS BE NON-EMPTY - any removal from the set that would
    //make it empty must result in the removal of this Node
    private Set<T> keys = new HashSet<>(1);

    Node<T> parent = null;
    Node<T> left = null;
    Node<T> right = null;

    int height = 0;
    int count = 0;

    Node(final T key) {
        //initialize HashSet with capacity 1 - we do not expect duplicates to be common
        //single entry is expected normal case
        add(key);
    }

    @Override
    public int compareTo(T o) {
        //choose an arbitrary entry from keys - they all have same compareTo value
        //and keys must be non-empty so we can choose first from iterator
        return -1 * o.compareTo(keys.iterator().next());
    }

    public boolean contains(T element){
        return keys.contains(element);
    }

    //element.compareTo(A), A any member of keys must == 0
    public void add(final T element){
        keys.add(element);
    }

    //must not remove single element - node must be deleted in this case
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