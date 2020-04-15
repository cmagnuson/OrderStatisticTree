package com.mtecresults.ranking;

import java.util.Set;

public interface OrderStatisticSet<T> {

    Set<T> get(int rank);

    //based 1 rank of an element in set, ties are allowed
    int rankOf(T element);

    boolean remove(T o);

    int size();

    boolean add(T element);
}
