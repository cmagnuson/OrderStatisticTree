package com.mtecresults.ranking;

import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class OrderStatisticTreeTest {

    private final OrderStatisticTree<Integer> tree = new OrderStatisticTree<>();
    private final TreeSet<Integer> set = new TreeSet<>();

    @Before
    public void before() {
        tree.clear();
        set.clear();
    }

    @Test
    public void testAdd() {
        assertEquals(set.isEmpty(), tree.isEmpty());

        for (int i = 10; i < 30; i += 2) {
            assertTrue(tree.isHealthy());
            assertEquals(set.contains(i), tree.contains(i));
            assertEquals(set.add(i), tree.add(i));
            assertEquals(set.add(i), tree.add(i));
            assertEquals(set.contains(i), tree.contains(i));

            assertFalse(tree.containsCycles());
            assertTrue(tree.heightsAreCorrect());
            assertTrue(tree.isBalanced());
            assertTrue(tree.isWellIndexed());
            assertTrue(tree.isHealthy());
        }

        assertEquals(set.isEmpty(), tree.isEmpty());
    }

    @Test
    public void testAddAll() {
        for (int i = 0; i < 10; ++i) {
            assertEquals(set.add(i), tree.add(i));
        }

        Collection<Integer> coll = Arrays.asList(10, 9, 7, 11, 12);

        assertEquals(set.addAll(coll), tree.addAll(coll));
        assertEquals(set.size(), tree.size());

        for (int i = -10; i < 20; ++i) {
            assertEquals(set.contains(i), tree.contains(i));
        }
    }

    @Test
    public void testClear() {
        for (int i = 0; i < 2000; ++i) {
            set.add(i);
            tree.add(i);
        }

        assertEquals(set.size(), tree.size());
        set.clear();
        tree.clear();
        assertEquals(set.size(), tree.size());
    }

    @Test
    public void testContains() {
        for (int i = 100; i < 200; i += 3) {
            assertTrue(tree.isHealthy());
            assertEquals(set.add(i), tree.add(i));
            assertTrue(tree.isHealthy());
        }

        assertEquals(set.size(), tree.size());

        for (int i = 0; i < 300; ++i) {
            assertEquals(set.contains(i), tree.contains(i));
        }
    }

    @Test
    public void testContainsAll() {
        for (int i = 0; i < 50; ++i) {
            set.add(i);
            tree.add(i);
        }

        Collection<Integer> coll = new HashSet<>();

        for (int i = 10; i < 20; ++i) {
            coll.add(i);
        }

        assertEquals(set.containsAll(coll), tree.containsAll(coll));
        coll.add(100);
        assertEquals(set.containsAll(coll), tree.containsAll(coll));
    }

    @Test
    public void testRemove() {
        for (int i = 0; i < 200; ++i) {
            assertEquals(set.add(i), tree.add(i));
        }

        for (int i = 50; i < 150; i += 2) {
            assertEquals(set.remove(i), tree.remove(i));
            assertTrue(tree.isHealthy());
        }

        for (int i = -100; i < 300; ++i) {
            assertEquals(set.contains(i), tree.contains(i));
        }
    }

    @Test
    public void testRemoveLast() {
        tree.add(1);
        tree.remove(1);
        assertEquals(0, tree.size());
    }

    @Test
    public void testRemoveAll() {
        for (int i = 0; i < 40; ++i) {
            set.add(i);
            tree.add(i);
        }

        Collection<Integer> coll = new HashSet<>();

        for (int i = 10; i < 20; ++i) {
            coll.add(i);
        }

        assertEquals(set.removeAll(coll), tree.removeAll(coll));

        for (int i = -10; i < 50; ++i) {
            assertEquals(set.contains(i), tree.contains(i));
        }

        assertEquals(set.removeAll(coll), tree.removeAll(coll));

        for (int i = -10; i < 50; ++i) {
            assertEquals(set.contains(i), tree.contains(i));
        }
    }

    @Test
    public void testSize() {
        for (int i = 0; i < 200; ++i) {
            assertEquals(set.size(), tree.size());
            assertEquals(set.add(i), tree.add(i));
            assertEquals(set.size(), tree.size());
        }
    }

    @Test
    public void testDuplicates() {
        OrderStatisticTree<IntegerWrapper> duplicateTree = new OrderStatisticTree<>();
        assertTrue(duplicateTree.add(new IntegerWrapper(1, 1)));
        assertTrue(duplicateTree.add(new IntegerWrapper(3, 2)));
        assertTrue(duplicateTree.add(new IntegerWrapper(3, 3)));
        //entering an existing element - this should have no effect
        assertFalse(duplicateTree.add(new IntegerWrapper(3, 3)));
        assertTrue(duplicateTree.add(new IntegerWrapper(3, 4)));
        assertTrue(duplicateTree.add(new IntegerWrapper(2, 5)));
        assertTrue(duplicateTree.add(new IntegerWrapper(3, 6)));
        assertTrue(duplicateTree.add(new IntegerWrapper(10, 7)));
        assertTrue(duplicateTree.add(new IntegerWrapper(9, 8)));
        assertTrue(duplicateTree.add(new IntegerWrapper(9, 9)));
        assertTrue(duplicateTree.add(new IntegerWrapper(3, 10)));

        //check IntegerWrapper equals/compareTo working as expected
        assertEquals(new IntegerWrapper(3, 1), new IntegerWrapper(4, 1));
        assertEquals(new IntegerWrapper(3, 3), new IntegerWrapper(3, 3));
        assertEquals(0, new IntegerWrapper(3, 1).compareTo(new IntegerWrapper(3, 2)));

        //there should be as many entries as we added - including duplicates but excluding those that are .equals()
        assertEquals(10, duplicateTree.size());

        //we should have these values and counts of each 1x 1, 1x 2, 5x 3, 2x 9, 1x 10
        //this is 0 indexed, so start with place 0, etc
        assertEquals(1, duplicateTree.rankOf(new IntegerWrapper(1, 1)));
        assertEquals(2, duplicateTree.rankOf(new IntegerWrapper(2, 5)));
        assertEquals(3, duplicateTree.rankOf(new IntegerWrapper(3, 3)));
        assertEquals(8, duplicateTree.rankOf(new IntegerWrapper(9, 9)));
        assertEquals(10, duplicateTree.rankOf(new IntegerWrapper(10, 7)));

        assertEquals(1, duplicateTree.get(1).size());
        assertEquals(1, duplicateTree.get(2).size());
        assertEquals(5, duplicateTree.get(3).size());
        assertEquals(2, duplicateTree.get(8).size());
        //check that place which is skipped because of a tie has no entries
        assertEquals(0, duplicateTree.get(9).size());
        assertEquals(1, duplicateTree.get(10).size());

        //remove some items and retest
        duplicateTree.remove(new IntegerWrapper(3, 2));
        duplicateTree.remove(new IntegerWrapper(2, 5));
        duplicateTree.remove(new IntegerWrapper(9, 8));
        duplicateTree.remove(new IntegerWrapper(9, 9));

        //there should be as many entries as we added - including duplicates but excluding those that are .equals()
        assertEquals(6, duplicateTree.size());

        //we should have these values and counts of each 1x 1, 4x 3, 1x 10
        //this is 0 indexed, so start with place 0, etc
        assertEquals(1, duplicateTree.rankOf(new IntegerWrapper(1, 1)));
        //no longer exists
        assertEquals(-1, duplicateTree.rankOf(new IntegerWrapper(2, 5)));
        //moved up in place
        assertEquals(2, duplicateTree.rankOf(new IntegerWrapper(3, 3)));
        assertEquals(-1, duplicateTree.rankOf(new IntegerWrapper(9, 9)));
        //moved up in place
        assertEquals(6, duplicateTree.rankOf(new IntegerWrapper(10, 7)));

        assertEquals(1, duplicateTree.get(1).size());
        assertEquals(4, duplicateTree.get(2).size());
        assertEquals(1, duplicateTree.get(6).size());
        //check that places which were skipped because of a tie has no entries
        assertEquals(0, duplicateTree.get(3).size());
        assertEquals(0, duplicateTree.get(4).size());


    }

    public static class IntegerWrapper implements Comparable<IntegerWrapper> {
        Integer value;
        int id;

        public IntegerWrapper(Integer value, int id) {
            this.value = value;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof IntegerWrapper && ((IntegerWrapper) o).id == id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public int compareTo(IntegerWrapper o) {
            return value.compareTo(o.value);
        }
    }

    @Test
    public void testIndexOf() {
        for (int i = 0; i < 100; ++i) {
            assertTrue(tree.add(i * 2));
        }

        for (int i = 0; i < 100; ++i) {
            assertEquals(i, tree.indexOf(2 * i));
        }

        for (int i = 100; i < 150; ++i) {
            assertEquals(-1, tree.indexOf(2 * i));
        }
    }

    @Test
    public void testEmpty() {
        assertEquals(set.isEmpty(), tree.isEmpty());
        set.add(0);
        tree.add(0);
        assertEquals(set.isEmpty(), tree.isEmpty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEmptyTreeGetThrowsOnNegativeIndex() {
        tree.get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testEmptyTreeSelectThrowsOnTooLargeIndex() {
        tree.get(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSelectThrowsOnNegativeIndex() {
        for (int i = 0; i < 5; ++i) {
            tree.add(i);
        }

        tree.get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSelectThrowsOnTooLargeIndex() {
        for (int i = 0; i < 5; ++i) {
            tree.add(i);
        }

        tree.get(5+1);
    }

    @Test
    public void testGet() {
        for (int i = 0; i < 100; i += 3) {
            tree.add(i);
        }

        for (int i = 0; i < tree.size(); ++i) {
            Set<Integer> ints = tree.get(i+1);
            assertEquals(Integer.valueOf(3 * i), ints.iterator().next());
        }
    }

    @Test
    public void findBug() {
        tree.add(0);
        assertTrue(tree.isHealthy());

        tree.add(-1);
        tree.remove(-1);
        assertTrue(tree.isHealthy());

        tree.add(1);
        tree.remove(1);
        assertTrue(tree.isHealthy());

        tree.add(-1);
        tree.add(1);
        tree.remove(0);
        assertTrue(tree.isHealthy());

        tree.clear();
        tree.add(0);
        tree.add(-1);
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(11);
        tree.add(30);
        tree.add(7);

        tree.remove(-1);

        assertTrue(tree.isHealthy());
    }

    @Test
    public void tryReproduceTheCounterBug() {
        long seed = System.nanoTime();
        Random random = new Random(seed);
        List<Integer> list = new ArrayList<>();

        System.out.println("tryReproduceTheCounterBug: seed = " + seed);

        for (int i = 0; i < 10; ++i) {
            int number = random.nextInt(1000);
            list.add(number);
            tree.add(number);
            assertTrue(tree.isHealthy());
        }

        for (Integer i : list) {
            tree.remove(i);
            boolean healthy = tree.isHealthy();
            assertTrue(healthy);
        }
    }
}
